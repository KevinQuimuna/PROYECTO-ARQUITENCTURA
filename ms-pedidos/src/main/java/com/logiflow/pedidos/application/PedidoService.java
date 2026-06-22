package com.logiflow.pedidos.application;

import com.logiflow.pedidos.api.PedidoResponse;
import com.logiflow.pedidos.api.UpsertPedidoRequest;
import com.logiflow.pedidos.domain.EstadoPedido;
import com.logiflow.pedidos.domain.Paquete;
import com.logiflow.pedidos.domain.Pedido;
import com.logiflow.pedidos.domain.PedidoRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final PedidoEventPublisher events;

    public PedidoService(PedidoRepository pedidos, PedidoEventPublisher events) {
        this.pedidos = pedidos;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidos.findAll().stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarActivosPorCliente(Long clienteId) {
        return pedidos.findByClienteIdAndEstadoNot(clienteId, EstadoPedido.CANCELADO).stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id) {
        return pedidos.findById(id).map(PedidoResponse::from).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PedidoResponse porCodigo(String codigo) {
        return pedidos
                .findByCodigoSeguimiento(codigo)
                .map(PedidoResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public PedidoResponse crear(UpsertPedidoRequest req) {
        Pedido p = new Pedido();
        p.setCodigoSeguimiento(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        aplicar(p, req);
        if (req.paquetes() != null) {
            for (var linea : req.paquetes()) {
                Paquete q = new Paquete();
                q.setPedido(p);
                q.setDescripcion(linea.descripcion());
                q.setPesoKg(linea.pesoKg());
                p.getPaquetes().add(q);
            }
        }
        p = pedidos.save(p);
        events.publicarCreado(p);
        return PedidoResponse.from(p);
    }

    @Transactional
    public PedidoResponse cancelar(Long id) {
        Pedido p = pedidos.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (p.getEstado() == EstadoPedido.ENTREGADO || p.getEstado() == EstadoPedido.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estado no permite cancelación");
        }
        p.setEstado(EstadoPedido.CANCELADO);
        p.setUpdatedAt(Instant.now());
        pedidos.save(p);
        events.publicarCancelado(p);
        return PedidoResponse.from(p);
    }

    @Transactional
    public PedidoResponse actualizarEstado(Long id, EstadoPedido nuevo) {
        Pedido p = pedidos.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        p.setEstado(nuevo);
        p.setUpdatedAt(Instant.now());
        return PedidoResponse.from(pedidos.save(p));
    }

    private static void aplicar(Pedido p, UpsertPedidoRequest req) {
        p.setClienteId(req.clienteId());
        p.setOrigenDireccion(req.origenDireccion());
        p.setOrigenLat(req.origenLat());
        p.setOrigenLng(req.origenLng());
        p.setDestinoDireccion(req.destinoDireccion());
        p.setDestinoLat(req.destinoLat());
        p.setDestinoLng(req.destinoLng());
        p.setPesoKg(req.pesoKg());
        p.setNivel(req.nivel());
        p.setPrioridad(req.prioridad() != null ? req.prioridad() : com.logiflow.pedidos.domain.PrioridadPedido.MEDIA);
    }
}

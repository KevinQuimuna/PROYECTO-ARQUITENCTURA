package com.logiflow.ruteo.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.logiflow.common.events.EnvioAsignadoEvent;
import com.logiflow.common.events.PedidoCreadoEvent;
import com.logiflow.common.events.PedidoEntregadoEvent;
import com.logiflow.ruteo.api.AsignacionManualRequest;
import com.logiflow.ruteo.api.EnvioResponse;
import com.logiflow.ruteo.domain.Envio;
import com.logiflow.ruteo.domain.EnvioRepository;
import com.logiflow.ruteo.domain.EstadoEnvio;
import com.logiflow.ruteo.domain.Parada;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnvioService {

    private static final double DEFAULT_LAT = -34.6037;
    private static final double DEFAULT_LNG = -58.3816;

    private final EnvioRepository envios;
    private final FlotaRestClient flota;
    private final PedidoRestClient pedidos;
    private final RuteoEventPublisher publisher;

    public EnvioService(
            EnvioRepository envios,
            FlotaRestClient flota,
            PedidoRestClient pedidos,
            RuteoEventPublisher publisher) {
        this.envios = envios;
        this.flota = flota;
        this.pedidos = pedidos;
        this.publisher = publisher;
    }

    @Transactional
    public void procesarPedidoCreado(PedidoCreadoEvent evt) {
        if (envios.findByPedidoId(evt.pedidoId()).isPresent()) {
            return;
        }
        JsonNode pedido = pedidos.obtenerPedido(evt.pedidoId());
        double peso = pedido.path("pesoKg").asDouble(evt.pesoKg());
        List<JsonNode> candidatos = flota.listarDisponibles(peso);
        if (candidatos.isEmpty()) {
            return;
        }
        String vehiculoId = candidatos.get(0).path("id").asText();
        asignarInterno(evt.pedidoId(), pedido, vehiculoId);
    }

    @Transactional
    public void procesarPedidoCancelado(Long pedidoId) {
        envios.findByPedidoId(pedidoId).ifPresent(e -> {
            if (e.getEstado() == EstadoEnvio.ENTREGADO || e.getEstado() == EstadoEnvio.CANCELADO) {
                return;
            }
            e.setEstado(EstadoEnvio.CANCELADO);
            e.setUpdatedAt(Instant.now());
            envios.save(e);
            if (e.getVehiculoId() != null) {
                JsonNode v = flota.obtenerVehiculo(e.getVehiculoId());
                if (v != null && "EN_SERVICIO".equals(v.path("estado").asText())) {
                    flota.actualizarEstadoVehiculo(v, "DISPONIBLE");
                }
            }
        });
    }

    @Transactional
    public EnvioResponse asignacionManual(AsignacionManualRequest req) {
        JsonNode pedido = pedidos.obtenerPedido(req.pedidoId());
        envios.findByPedidoId(req.pedidoId()).ifPresent(e -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido ya tiene envío");
        });
        return asignarInterno(req.pedidoId(), pedido, req.vehiculoId());
    }

    private EnvioResponse asignarInterno(long pedidoId, JsonNode pedido, String vehiculoId) {
        JsonNode v = flota.obtenerVehiculo(vehiculoId);
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehículo inválido");
        }

        Envio e = new Envio();
        e.setPedidoId(pedidoId);
        e.setCodigoSeguimiento(pedido.path("codigoSeguimiento").asText());
        e.setVehiculoId(vehiculoId);
        e.setConductorId(flota.buscarConductorPorVehiculo(vehiculoId));
        e.setTipoVehiculo(v.path("tipo").asText());
        e.setEstado(EstadoEnvio.ASIGNADO);

        double oLat = pedido.path("origenLat").isNull() ? DEFAULT_LAT : pedido.path("origenLat").asDouble();
        double oLng = pedido.path("origenLng").isNull() ? DEFAULT_LNG : pedido.path("origenLng").asDouble();
        double dLat = pedido.path("destinoLat").isNull() ? DEFAULT_LAT + 0.05 : pedido.path("destinoLat").asDouble();
        double dLng = pedido.path("destinoLng").isNull() ? DEFAULT_LNG + 0.05 : pedido.path("destinoLng").asDouble();

        double distKm = haversineKm(oLat, oLng, dLat, dLng);
        e.setKmsEstimados(BigDecimal.valueOf(distKm).setScale(2, RoundingMode.HALF_UP));
        e.setEtaMinutos((int) (distKm * 2.5 + 15));
        e.setRutaResumen("Origen → Destino (" + distKm + " km aprox.)");

        Parada p0 = new Parada();
        p0.setEnvio(e);
        p0.setOrdenParada(0);
        p0.setLat(oLat);
        p0.setLng(oLng);
        p0.setTipo("RECOGIDA");
        e.getParadas().add(p0);

        Parada p1 = new Parada();
        p1.setEnvio(e);
        p1.setOrdenParada(1);
        p1.setLat(dLat);
        p1.setLng(dLng);
        p1.setTipo("ENTREGA");
        e.getParadas().add(p1);

        e = envios.save(e);

        pedidos.actualizarEstado(pedidoId, "ASIGNADO");
        flota.actualizarEstadoVehiculo(v, "EN_SERVICIO");

        publisher.envioAsignado(new EnvioAsignadoEvent(
                e.getId(),
                pedidoId,
                e.getCodigoSeguimiento(),
                vehiculoId,
                e.getConductorId(),
                e.getTipoVehiculo(),
                e.getKmsEstimados() != null ? e.getKmsEstimados().doubleValue() : null,
                e.getEtaMinutos()));

        return EnvioResponse.from(e);
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional(readOnly = true)
    public EnvioResponse obtener(Long id) {
        return envios.findById(id).map(EnvioResponse::from).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public EnvioResponse porCodigo(String codigo) {
        return envios
                .findByCodigoSeguimiento(codigo)
                .map(EnvioResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public EnvioResponse iniciarRuta(Long envioId) {
        Envio e = envios.findById(envioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (e.getEstado() != EstadoEnvio.ASIGNADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El envío no está asignado");
        }
        e.setEstado(EstadoEnvio.EN_RUTA);
        e.setUpdatedAt(Instant.now());
        envios.save(e);
        pedidos.actualizarEstado(e.getPedidoId(), "EN_RUTA");
        return EnvioResponse.from(e);
    }

    @Transactional
    public EnvioResponse entregar(Long envioId) {
        Envio e = envios.findById(envioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (e.getEstado() != EstadoEnvio.EN_RUTA && e.getEstado() != EstadoEnvio.ASIGNADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estado no permite entrega");
        }
        if (e.getConductorId() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede entregar un envío sin conductor asignado");
        }
        e.setEstado(EstadoEnvio.ENTREGADO);
        e.setUpdatedAt(Instant.now());
        envios.save(e);
        pedidos.actualizarEstado(e.getPedidoId(), "ENTREGADO");

        JsonNode pedido = pedidos.obtenerPedido(e.getPedidoId());
        JsonNode v = flota.obtenerVehiculo(e.getVehiculoId());
        if (v != null) {
            flota.actualizarEstadoVehiculo(v, "DISPONIBLE");
        }

        publisher.pedidoEntregado(new PedidoEntregadoEvent(
                e.getPedidoId(),
                e.getCodigoSeguimiento(),
                pedido.path("clienteId").asLong(),
                e.getId(),
                pedido.path("nivel").asText(),
                pedido.path("pesoKg").asDouble(),
                e.getTipoVehiculo(),
                e.getKmsEstimados() != null ? e.getKmsEstimados().doubleValue() : 0));

        return EnvioResponse.from(e);
    }

    @Transactional(readOnly = true)
    public List<Envio> listarEnRuta() {
        return envios.findWithParadasByEstado(EstadoEnvio.EN_RUTA);
    }

    @Transactional(readOnly = true)
    public List<EnvioResponse> listarTodos() {
        return envios.findAll().stream()
                .map(EnvioResponse::from)
                .toList();
    }
}

package com.logiflow.pedidos.api;

import com.logiflow.pedidos.application.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidos;

    public PedidoController(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return pedidos.listarTodos();
    }

    @GetMapping("/{id}")
    public PedidoResponse get(@PathVariable Long id) {
        return pedidos.obtener(id);
    }

    @GetMapping("/codigo/{codigo}")
    public PedidoResponse porCodigo(@PathVariable String codigo) {
        return pedidos.porCodigo(codigo);
    }

    @GetMapping("/activos")
    public List<PedidoResponse> activosPorCliente(@RequestParam Long clienteId) {
        return pedidos.listarActivosPorCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody UpsertPedidoRequest body) {
        return pedidos.crear(body);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return pedidos.cancelar(id);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse estado(@PathVariable Long id, @Valid @RequestBody EstadoPedidoRequest body) {
        return pedidos.actualizarEstado(id, body.estado());
    }
}

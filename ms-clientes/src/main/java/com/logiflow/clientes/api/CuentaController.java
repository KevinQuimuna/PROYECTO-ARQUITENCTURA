package com.logiflow.clientes.api;

import com.logiflow.clientes.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas-corporativas")
@Tag(name = "Cuentas corporativas", description = "CRUD de cuentas asociadas a clientes corporativos.")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las cuentas")
    public List<CuentaDto> listar() {
        return service.listarTodas();
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar cuentas de un cliente")
    public List<CuentaDto> byCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por id")
    public CuentaDto get(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cuenta", description = "No envíe id: se genera automáticamente.")
    public CuentaDto create(@Valid @RequestBody CreateCuentaRequest body) {
        return service.crear(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta")
    public CuentaDto update(@PathVariable Long id, @Valid @RequestBody UpdateCuentaRequest body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar cuenta")
    public void delete(@PathVariable Long id) {
        service.eliminar(id);
    }
}

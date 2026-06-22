package com.logiflow.clientes.api;

import com.logiflow.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "CRUD de clientes. El id y el código se generan al crear.")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping("/health")
    @Operation(summary = "Estado del servicio", description = "Verifica que ms-clientes responde correctamente.")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "ms-clientes");
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public List<ClienteDto> list() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por id")
    public ClienteDto get(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cliente", description = "No envíe id ni codigo: se generan automáticamente.")
    public ClienteDto create(@Valid @RequestBody CreateClienteRequest body) {
        return service.crear(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ClienteDto update(@PathVariable Long id, @Valid @RequestBody UpdateClienteRequest body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar cliente")
    public void delete(@PathVariable Long id) {
        service.eliminar(id);
    }
}

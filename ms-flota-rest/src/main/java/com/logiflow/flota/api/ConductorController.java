package com.logiflow.flota.api;

import com.logiflow.flota.api.dto.ConductorRequest;
import com.logiflow.flota.api.dto.ConductorResponse;
import com.logiflow.flota.service.ConductorService;
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
@RequestMapping("/api/v1/conductores")
@Tag(name = "Conductores", description = "CRUD de conductores de flota")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    @Operation(summary = "Listar conductores")
    public List<ConductorResponse> listar() {
        return conductorService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conductor por id")
    public ConductorResponse obtener(@PathVariable String id) {
        return conductorService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear conductor")
    public ConductorResponse crear(@Valid @RequestBody ConductorRequest request) {
        return conductorService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar conductor")
    public ConductorResponse actualizar(@PathVariable String id, @Valid @RequestBody ConductorRequest request) {
        return conductorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar conductor")
    public void eliminar(@PathVariable String id) {
        conductorService.eliminar(id);
    }
}

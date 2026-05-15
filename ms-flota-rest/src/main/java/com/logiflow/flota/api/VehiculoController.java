package com.logiflow.flota.api;

import com.logiflow.flota.api.dto.VehiculoRequest;
import com.logiflow.flota.api.dto.VehiculoResponse;
import com.logiflow.flota.domain.TipoVehiculo;
import com.logiflow.flota.service.VehiculoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehiculos")
@Tag(name = "Vehículos", description = "CRUD y consultas de disponibilidad para ruteo")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    @Operation(summary = "Listar vehículos")
    public List<VehiculoResponse> listar() {
        return vehiculoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por id")
    public VehiculoResponse obtener(@PathVariable String id) {
        return vehiculoService.obtener(id);
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Obtener vehículo por matrícula")
    public VehiculoResponse porMatricula(@PathVariable String matricula) {
        return vehiculoService.porMatricula(matricula);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Vehículos disponibles para asignación (Ruteo)", description = "Filtra por tipo opcional")
    public List<VehiculoResponse> disponibles(@RequestParam(required = false) TipoVehiculo tipo) {
        return vehiculoService.disponibles(tipo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear vehículo")
    public VehiculoResponse crear(@Valid @RequestBody VehiculoRequest request) {
        return vehiculoService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehículo")
    public VehiculoResponse actualizar(@PathVariable String id, @Valid @RequestBody VehiculoRequest request) {
        return vehiculoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar vehículo")
    public void eliminar(@PathVariable String id) {
        vehiculoService.eliminar(id);
    }
}

package com.logiflow.flota.api;

import com.logiflow.flota.api.dto.ConductorResponse;
import com.logiflow.flota.service.ConductorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints orientados al contexto de Ruteo: disponibilidad humana.
 */
@RestController
@RequestMapping("/api/v1/ruteo")
@Tag(name = "Ruteo (consulta flota)", description = "Operaciones de solo lectura para asignación")
public class RuteoConsultaController {

    private final ConductorService conductorService;

    public RuteoConsultaController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping("/conductores-disponibles")
    @Operation(summary = "Conductores disponibles para asignación")
    public List<ConductorResponse> conductoresDisponibles() {
        return conductorService.disponiblesParaRuteo();
    }
}

package com.logiflow.taller.api;

import com.logiflow.taller.api.dto.ConsultaVehiculoTallerResponse;
import com.logiflow.taller.api.dto.RegistrarOrdenMantenimientoRequest;
import com.logiflow.taller.api.dto.RegistrarOrdenMantenimientoResponse;
import com.logiflow.taller.service.TallerAclService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taller/v1")
@Tag(name = "Taller (ACL)", description = "Contrato REST hacia el sistema de talleres externo")
public class TallerController {

    private final TallerAclService tallerAclService;

    public TallerController(TallerAclService tallerAclService) {
        this.tallerAclService = tallerAclService;
    }

    @GetMapping("/vehiculos/{matricula}")
    @Operation(summary = "consultarVehiculo(matricula)", description = "Devuelve datos en formato del taller externo")
    public ConsultaVehiculoTallerResponse consultarVehiculo(@PathVariable String matricula) {
        return tallerAclService.consultarVehiculo(matricula);
    }

    @PostMapping("/ordenes-mantenimiento")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "registrarOrdenMantenimiento(matricula, descripcion)")
    public RegistrarOrdenMantenimientoResponse registrarOrden(@Valid @RequestBody RegistrarOrdenMantenimientoRequest body) {
        return tallerAclService.registrarOrdenMantenimiento(body);
    }
}

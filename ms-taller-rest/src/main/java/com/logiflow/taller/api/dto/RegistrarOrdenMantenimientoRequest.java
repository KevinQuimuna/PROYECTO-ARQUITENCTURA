package com.logiflow.taller.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarOrdenMantenimientoRequest(
        @NotBlank @Size(max = 32) @Schema(example = "ABC1234") String matricula,
        @NotBlank @Size(max = 2000) @Schema(example = "Cambio de pastillas y revisión de frenos") String descripcion) {}

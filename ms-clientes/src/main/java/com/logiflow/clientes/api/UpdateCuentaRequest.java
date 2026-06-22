package com.logiflow.clientes.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Schema(description = "Datos para actualizar una cuenta corporativa.")
public record UpdateCuentaRequest(
        @NotBlank @Schema(example = "Cuenta principal") String nombreCuenta,
        @Schema(example = "CTR-2026-001") String contratoNumero,
        @Schema(example = "15000.00") BigDecimal saldo,
        @Schema(example = "50000.00") BigDecimal limiteCredito) {}

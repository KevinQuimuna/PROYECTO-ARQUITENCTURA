package com.logiflow.clientes.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Cuenta corporativa de un cliente.")
public record CuentaDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1") Long id,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1") Long clienteId,
        @Schema(example = "Cuenta principal") String nombreCuenta,
        @Schema(example = "CTR-2026-001") String contratoNumero,
        @Schema(example = "15000.00") BigDecimal saldo,
        @Schema(example = "50000.00") BigDecimal limiteCredito) {}

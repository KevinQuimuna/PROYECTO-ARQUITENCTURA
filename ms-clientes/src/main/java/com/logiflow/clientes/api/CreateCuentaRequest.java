package com.logiflow.clientes.api;

import com.logiflow.clientes.domain.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Datos para crear una cuenta corporativa. El id se genera automáticamente.")
public record CreateCuentaRequest(
        @NotNull @Schema(example = "1") Long clienteId,
        @NotBlank @Schema(example = "Cuenta principal") String nombreCuenta,
        @Schema(example = "CTR-2026-001") String contratoNumero,
        @Schema(example = "0.00") BigDecimal saldo,
        @Schema(example = "50000.00") BigDecimal limiteCredito) {}

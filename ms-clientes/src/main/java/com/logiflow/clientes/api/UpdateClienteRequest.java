package com.logiflow.clientes.api;

import com.logiflow.clientes.domain.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para actualizar un cliente. El código no se puede modificar.")
public record UpdateClienteRequest(
        @NotBlank @Schema(example = "Acme Corp S.A.") String razonSocial,
        @Schema(example = "contacto@acme.test") String email,
        @Schema(example = "+5491100000001") String telefono,
        @Schema(example = "CORPORATIVO") TipoCliente tipo,
        @Schema(example = "true") boolean activo) {}

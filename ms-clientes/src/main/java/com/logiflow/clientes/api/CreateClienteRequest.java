package com.logiflow.clientes.api;

import com.logiflow.clientes.domain.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear un cliente. El id y el código se generan automáticamente.")
public record CreateClienteRequest(
        @NotBlank @Schema(example = "Acme Corp S.A.") String razonSocial,
        @Schema(example = "contacto@acme.test") String email,
        @Schema(example = "+5491100000001") String telefono,
        @Schema(example = "CORPORATIVO", description = "PARTICULAR o CORPORATIVO. Por defecto: PARTICULAR")
                TipoCliente tipo,
        @Schema(example = "true", description = "Por defecto: true") Boolean activo) {}

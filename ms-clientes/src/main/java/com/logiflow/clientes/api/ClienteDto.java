package com.logiflow.clientes.api;

import com.logiflow.clientes.domain.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cliente registrado en el sistema.")
public record ClienteDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1") Long id,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "CLI-A1B2C3D4") String codigo,
        @Schema(example = "Acme Corp S.A.") String razonSocial,
        @Schema(example = "contacto@acme.test") String email,
        @Schema(example = "+5491100000001") String telefono,
        @Schema(example = "CORPORATIVO") TipoCliente tipo,
        @Schema(example = "true") boolean activo) {

    public static ClienteDto from(com.logiflow.clientes.domain.Cliente c) {
        return new ClienteDto(
                c.getId(), c.getCodigo(), c.getRazonSocial(), c.getEmail(), c.getTelefono(), c.getTipo(), c.isActivo());
    }
}

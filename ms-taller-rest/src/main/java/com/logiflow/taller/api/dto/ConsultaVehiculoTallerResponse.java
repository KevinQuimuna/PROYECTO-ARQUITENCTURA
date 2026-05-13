package com.logiflow.taller.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Formato agregado esperado por el sistema de talleres externo (vista anticorrupción).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de consulta de vehículo para el taller legado")
public class ConsultaVehiculoTallerResponse {

    @Schema(example = "OK", description = "OK o NOT_FOUND")
    private String codigoRespuesta;

    private VehiculoTallerExterno vehiculo;

    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public VehiculoTallerExterno getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoTallerExterno vehiculo) {
        this.vehiculo = vehiculo;
    }

    public static ConsultaVehiculoTallerResponse ok(VehiculoTallerExterno v) {
        ConsultaVehiculoTallerResponse r = new ConsultaVehiculoTallerResponse();
        r.setCodigoRespuesta("OK");
        r.setVehiculo(v);
        return r;
    }

    public static ConsultaVehiculoTallerResponse notFound() {
        ConsultaVehiculoTallerResponse r = new ConsultaVehiculoTallerResponse();
        r.setCodigoRespuesta("NOT_FOUND");
        r.setVehiculo(null);
        return r;
    }
}

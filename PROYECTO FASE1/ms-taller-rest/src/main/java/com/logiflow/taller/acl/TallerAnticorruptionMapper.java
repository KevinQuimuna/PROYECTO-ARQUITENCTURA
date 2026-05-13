package com.logiflow.taller.acl;

import com.logiflow.taller.api.dto.VehiculoTallerExterno;
import com.logiflow.taller.integration.flota.VehiculoFlotaDto;
import org.springframework.stereotype.Component;

/**
 * Traduce el modelo interno de Flota al lenguaje del sistema de talleres externo.
 */
@Component
public class TallerAnticorruptionMapper {

    public VehiculoTallerExterno toExterno(VehiculoFlotaDto f) {
        if (f == null) {
            return null;
        }
        VehiculoTallerExterno v = new VehiculoTallerExterno();
        v.setMatricula(f.getMatricula());
        v.setTipoEquipo(mapearTipoEquipo(f.getTipo()));
        v.setMasaMaximaKg(f.getCapacidadKg());
        v.setEstadoOperativoTaller(mapearEstado(f.getEstado()));
        v.setAutonomiaDeclaradaKm(f.getAutonomiaKm());
        return v;
    }

    private String mapearTipoEquipo(String tipoFlota) {
        if (tipoFlota == null) {
            return "EQ-DESCONOCIDO";
        }
        return switch (tipoFlota) {
            case "MOTO" -> "EQ-MOTO";
            case "AUTO" -> "EQ-AUTO";
            case "FURGONETA" -> "EQ-FURGON";
            case "CAMION" -> "EQ-CAMION";
            default -> "EQ-OTRO";
        };
    }

    private String mapearEstado(String estadoFlota) {
        if (estadoFlota == null) {
            return "DESCONOCIDO";
        }
        return switch (estadoFlota) {
            case "DISPONIBLE" -> "OPERATIVO";
            case "EN_SERVICIO" -> "EN_SERVICIO";
            case "MANTENIMIENTO" -> "EN_TALLER";
            default -> estadoFlota;
        };
    }
}

package com.logiflow.taller.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Vista de vehículo en terminología del taller externo")
public class VehiculoTallerExterno {

    @Schema(example = "ABC1234")
    private String matricula;

    @Schema(description = "Código de tipo de equipo según catálogo del taller", example = "EQ-MOTO")
    private String tipoEquipo;

    @Schema(description = "Masa máxima admisible (kg)")
    private Double masaMaximaKg;

    @Schema(description = "OPERATIVO, EN_SERVICIO o EN_TALLER")
    private String estadoOperativoTaller;

    private Integer autonomiaDeclaradaKm;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public Double getMasaMaximaKg() {
        return masaMaximaKg;
    }

    public void setMasaMaximaKg(Double masaMaximaKg) {
        this.masaMaximaKg = masaMaximaKg;
    }

    public String getEstadoOperativoTaller() {
        return estadoOperativoTaller;
    }

    public void setEstadoOperativoTaller(String estadoOperativoTaller) {
        this.estadoOperativoTaller = estadoOperativoTaller;
    }

    public Integer getAutonomiaDeclaradaKm() {
        return autonomiaDeclaradaKm;
    }

    public void setAutonomiaDeclaradaKm(Integer autonomiaDeclaradaKm) {
        this.autonomiaDeclaradaKm = autonomiaDeclaradaKm;
    }
}

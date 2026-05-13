package com.logiflow.taller.integration.flota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que refleja el JSON expuesto por ms-flota-rest (modelo interno LogiFlow).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiculoFlotaDto {

    private Long id;
    private String matricula;
    private String tipo;
    private Double capacidadKg;
    private Integer autonomiaKm;
    private String estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getCapacidadKg() {
        return capacidadKg;
    }

    public void setCapacidadKg(Double capacidadKg) {
        this.capacidadKg = capacidadKg;
    }

    public Integer getAutonomiaKm() {
        return autonomiaKm;
    }

    public void setAutonomiaKm(Integer autonomiaKm) {
        this.autonomiaKm = autonomiaKm;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

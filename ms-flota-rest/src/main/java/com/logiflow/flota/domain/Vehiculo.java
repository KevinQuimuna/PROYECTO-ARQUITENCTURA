package com.logiflow.flota.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    private String id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 32)
    private String matricula;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipo;

    @NotNull
    @PositiveOrZero
    private Double capacidadKg;

    /** Autonomía aproximada en km (opcional para ruteo). */
    @PositiveOrZero
    private Integer autonomiaKm;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
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

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 12);
        }
    }
}

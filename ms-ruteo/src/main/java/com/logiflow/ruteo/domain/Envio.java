package com.logiflow.ruteo.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(name = "codigo_seguimiento", nullable = false, length = 32)
    private String codigoSeguimiento;

    @Column(name = "vehiculo_id", length = 12)
    private String vehiculoId;

    @Column(name = "conductor_id", length = 12)
    private String conductorId;

    @Column(name = "tipo_vehiculo", length = 20)
    private String tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;

    @Column(name = "kms_estimados", precision = 10, scale = 2)
    private BigDecimal kmsEstimados;

    @Column(name = "eta_minutos")
    private Integer etaMinutos;

    @Column(name = "ruta_resumen", length = 1024)
    private String rutaResumen;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parada> paradas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public BigDecimal getKmsEstimados() {
        return kmsEstimados;
    }

    public void setKmsEstimados(BigDecimal kmsEstimados) {
        this.kmsEstimados = kmsEstimados;
    }

    public Integer getEtaMinutos() {
        return etaMinutos;
    }

    public void setEtaMinutos(Integer etaMinutos) {
        this.etaMinutos = etaMinutos;
    }

    public String getRutaResumen() {
        return rutaResumen;
    }

    public void setRutaResumen(String rutaResumen) {
        this.rutaResumen = rutaResumen;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Parada> getParadas() {
        return paradas;
    }
}

package com.logiflow.flota.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logiflow.flota.api.dto.VehiculoRequest;
import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;
import com.logiflow.flota.repo.ConductorRepository;
import com.logiflow.flota.repo.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class VehiculoServiceTest {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private VehiculoService vehiculoService;

    @BeforeEach
    void limpiar() {
        conductorRepository.deleteAll();
        vehiculoRepository.deleteAll();
    }

    @Test
    void crearYListarDisponibles() {
        vehiculoService.crear(new VehiculoRequest("AA-111", TipoVehiculo.AUTO, 400d, 300, EstadoVehiculo.DISPONIBLE));
        assertThat(vehiculoService.disponibles(null)).hasSize(1);
        assertThat(vehiculoService.disponibles(TipoVehiculo.MOTO)).isEmpty();
    }

    @Test
    void matriculaDuplicada() {
        vehiculoService.crear(new VehiculoRequest("DUPLI", TipoVehiculo.MOTO, 30d, 100, EstadoVehiculo.DISPONIBLE));
        assertThatThrownBy(() ->
                        vehiculoService.crear(new VehiculoRequest("dupli", TipoVehiculo.MOTO, 30d, 100, EstadoVehiculo.DISPONIBLE)))
                .isInstanceOf(ResponseStatusException.class);
    }
}

package com.logiflow.flota.config;

import com.logiflow.flota.domain.Conductor;
import com.logiflow.flota.domain.EstadoConductor;
import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;
import com.logiflow.flota.domain.Vehiculo;
import com.logiflow.flota.repo.ConductorRepository;
import com.logiflow.flota.repo.VehiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DemoDataLoader {

    @Bean
    CommandLineRunner cargarDemo(VehiculoRepository vehiculos, ConductorRepository conductores) {
        return args -> {
            if (vehiculos.count() > 0) {
                return;
            }
            Vehiculo v1 = new Vehiculo();
            v1.setMatricula("ABC1234");
            v1.setTipo(TipoVehiculo.MOTO);
            v1.setCapacidadKg(25d);
            v1.setAutonomiaKm(120);
            v1.setEstado(EstadoVehiculo.DISPONIBLE);
            vehiculos.save(v1);

            Vehiculo v2 = new Vehiculo();
            v2.setMatricula("XYZ5678");
            v2.setTipo(TipoVehiculo.FURGONETA);
            v2.setCapacidadKg(800d);
            v2.setAutonomiaKm(450);
            v2.setEstado(EstadoVehiculo.DISPONIBLE);
            vehiculos.save(v2);

            Vehiculo v3 = new Vehiculo();
            v3.setMatricula("CAM9999");
            v3.setTipo(TipoVehiculo.CAMION);
            v3.setCapacidadKg(12000d);
            v3.setAutonomiaKm(800);
            v3.setEstado(EstadoVehiculo.MANTENIMIENTO);
            vehiculos.save(v3);

            Conductor c1 = new Conductor();
            c1.setNombreCompleto("María López");
            c1.setLicencia("LIC-001-EC");
            c1.setEstado(EstadoConductor.DISPONIBLE);
            c1.setVehiculoAsignado(v1);
            conductores.save(c1);

            Conductor c2 = new Conductor();
            c2.setNombreCompleto("Carlos Ruiz");
            c2.setLicencia("LIC-002-EC");
            c2.setEstado(EstadoConductor.EN_SERVICIO);
            c2.setVehiculoAsignado(v2);
            conductores.save(c2);
        };
    }
}

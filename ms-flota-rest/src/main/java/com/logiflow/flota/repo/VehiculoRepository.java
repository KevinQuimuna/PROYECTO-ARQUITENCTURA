package com.logiflow.flota.repo;

import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;
import com.logiflow.flota.domain.Vehiculo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {

    Optional<Vehiculo> findByMatriculaIgnoreCase(String matricula);

    boolean existsByMatriculaIgnoreCase(String matricula);

    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    List<Vehiculo> findByEstadoAndTipo(EstadoVehiculo estado, TipoVehiculo tipo);
}

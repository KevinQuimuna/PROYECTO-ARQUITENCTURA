package com.logiflow.flota.repo;

import com.logiflow.flota.domain.Conductor;
import com.logiflow.flota.domain.EstadoConductor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConductorRepository extends JpaRepository<Conductor, String> {

    List<Conductor> findByEstado(EstadoConductor estado);

    boolean existsByLicencia(String licencia);
}

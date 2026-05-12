package com.logiflow.taller.repo;

import com.logiflow.taller.domain.OrdenMantenimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenMantenimientoRepository extends JpaRepository<OrdenMantenimiento, Long> {

    List<OrdenMantenimiento> findByMatriculaIgnoreCaseOrderByFechaRegistroDesc(String matricula);
}

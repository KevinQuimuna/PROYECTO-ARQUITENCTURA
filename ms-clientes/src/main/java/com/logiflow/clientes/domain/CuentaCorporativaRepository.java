package com.logiflow.clientes.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaCorporativaRepository extends JpaRepository<CuentaCorporativa, Long> {

    List<CuentaCorporativa> findByClienteId(Long clienteId);
}

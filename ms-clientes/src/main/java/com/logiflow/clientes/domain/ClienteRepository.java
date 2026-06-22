package com.logiflow.clientes.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}

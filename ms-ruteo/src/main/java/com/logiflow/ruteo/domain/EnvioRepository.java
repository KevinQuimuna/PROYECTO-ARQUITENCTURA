package com.logiflow.ruteo.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByPedidoId(Long pedidoId);

    Optional<Envio> findByCodigoSeguimiento(String codigo);

    @EntityGraph(attributePaths = "paradas")
    List<Envio> findWithParadasByEstado(EstadoEnvio estado);
}

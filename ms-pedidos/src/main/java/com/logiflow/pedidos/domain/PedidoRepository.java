package com.logiflow.pedidos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByCodigoSeguimiento(String codigo);

    List<Pedido> findByClienteIdAndEstadoNot(Long clienteId, EstadoPedido excluir);

    List<Pedido> findByEstadoIn(List<EstadoPedido> estados);
}

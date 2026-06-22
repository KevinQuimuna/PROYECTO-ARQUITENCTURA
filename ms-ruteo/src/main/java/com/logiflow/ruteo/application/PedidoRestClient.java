package com.logiflow.ruteo.application;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PedidoRestClient {

    private final RestClient pedidos;

    public PedidoRestClient(@Qualifier("pedidosHttpRestClient") RestClient pedidosClient) {
        this.pedidos = pedidosClient;
    }

    public JsonNode obtenerPedido(Long id) {
        try {
            return pedidos.get().uri("/api/pedidos/{id}", id).retrieve().body(JsonNode.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
            }
            throw e;
        }
    }

    public void actualizarEstado(Long pedidoId, String estado) {
        pedidos
                .patch()
                .uri("/api/pedidos/{id}/estado", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("estado", estado))
                .retrieve()
                .toBodilessEntity();
    }
}

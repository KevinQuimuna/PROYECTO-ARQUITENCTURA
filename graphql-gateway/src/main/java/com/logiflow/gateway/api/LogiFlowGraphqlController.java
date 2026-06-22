package com.logiflow.gateway.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.logiflow.common.events.PosicionActualizadaEvent;
import com.logiflow.gateway.application.PosicionStreamService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

@Controller
public class LogiFlowGraphqlController {

    private final RestClient pedidos;
    private final RestClient ruteo;
    private final PosicionStreamService posiciones;

    public LogiFlowGraphqlController(
            @Qualifier("pedidosRestClient") RestClient pedidos,
            @Qualifier("ruteoRestClient") RestClient ruteo,
            PosicionStreamService posiciones) {
        this.pedidos = pedidos;
        this.ruteo = ruteo;
        this.posiciones = posiciones;
    }

    @QueryMapping
    public List<Map<String, Object>> pedidosActivos(@Argument Integer clienteId) {
        JsonNode arr = pedidos
                .get()
                .uri("/api/pedidos/activos?clienteId={cid}", clienteId)
                .retrieve()
                .body(JsonNode.class);
        if (arr == null || !arr.isArray()) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (JsonNode n : arr) {
            out.add(pedidoMap(n));
        }
        return out;
    }

    @QueryMapping
    public Map<String, Object> envio(@Argument Integer id) {
        JsonNode e = ruteo.get().uri("/api/envios/{id}", id).retrieve().body(JsonNode.class);
        if (e == null) {
            return null;
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.path("id").asInt());
        m.put("pedidoId", e.path("pedidoId").asInt());
        m.put("codigoSeguimiento", e.path("codigoSeguimiento").asText());
        m.put("estado", e.path("estado").asText());
        m.put("vehiculoId", e.path("vehiculoId").isNull() ? null : e.path("vehiculoId").asText());
        m.put("kmsEstimados", e.path("kmsEstimados").isNull() ? null : e.path("kmsEstimados").asDouble());
        m.put("etaMinutos", e.path("etaMinutos").isNull() ? null : e.path("etaMinutos").asInt());
        JsonNode p = pedidos
                .get()
                .uri("/api/pedidos/{id}", e.path("pedidoId").asInt())
                .retrieve()
                .body(JsonNode.class);
        m.put("pedidoEstado", p != null ? p.path("estado").asText() : null);
        PosicionActualizadaEvent ultima = posiciones.ultimaPorEnvio(e.path("id").asLong());
        m.put("ultimaPosicion", ultima != null ? posiciones.toMap(ultima) : null);
        return m;
    }

    @MutationMapping
    public Map<String, Object> crearPedido(@Argument CrearPedidoInput input) {
        Map<String, Object> body = new HashMap<>();
        body.put("clienteId", input.clienteId());
        body.put("origenDireccion", input.origenDireccion());
        body.put("origenLat", input.origenLat());
        body.put("origenLng", input.origenLng());
        body.put("destinoDireccion", input.destinoDireccion());
        body.put("destinoLat", input.destinoLat());
        body.put("destinoLng", input.destinoLng());
        body.put("pesoKg", input.pesoKg());
        body.put("nivel", input.nivel());
        body.put("prioridad", input.prioridad());
        JsonNode created = pedidos
                .post()
                .uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        return pedidoMap(created);
    }

    @MutationMapping
    public boolean cancelarPedido(@Argument Integer id) {
        pedidos.post().uri("/api/pedidos/{id}/cancelar", id).retrieve().toBodilessEntity();
        return true;
    }

    @SubscriptionMapping
    public Flux<Map<String, Object>> seguimientoPosicion(@Argument String codigoSeguimiento) {
        return posiciones.stream(codigoSeguimiento);
    }

    private static Map<String, Object> pedidoMap(JsonNode n) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", n.path("id").asInt());
        m.put("codigoSeguimiento", n.path("codigoSeguimiento").asText());
        m.put("clienteId", n.path("clienteId").asInt());
        m.put("estado", n.path("estado").asText());
        m.put("nivel", n.path("nivel").asText());
        m.put("pesoKg", n.path("pesoKg").asDouble());
        return m;
    }
}

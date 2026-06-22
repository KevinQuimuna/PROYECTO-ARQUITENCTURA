package com.logiflow.ruteo.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FlotaRestClient {

    private final RestClient flota;
    private final ObjectMapper mapper;

    public FlotaRestClient(@Qualifier("flotaHttpRestClient") RestClient flota, ObjectMapper mapper) {
        this.flota = flota;
        this.mapper = mapper;
    }

    public List<JsonNode> listarDisponibles(double pesoKg) {
        JsonNode arr = flota.get().uri("/api/v1/vehiculos/disponibles").retrieve().body(JsonNode.class);
        if (arr == null || !arr.isArray()) {
            return List.of();
        }
        List<JsonNode> out = new ArrayList<>();
        for (JsonNode v : arr) {
            if (v.path("capacidadKg").asDouble(0) >= pesoKg) {
                out.add(v);
            }
        }
        return out;
    }

    public JsonNode obtenerVehiculo(String id) {
        return flota.get().uri("/api/v1/vehiculos/{id}", id).retrieve().body(JsonNode.class);
    }

    public void actualizarEstadoVehiculo(JsonNode vehiculo, String nuevoEstado) {
        ObjectNode body = mapper.createObjectNode();
        body.put("matricula", vehiculo.path("matricula").asText());
        body.put("tipo", vehiculo.path("tipo").asText());
        body.put("capacidadKg", vehiculo.path("capacidadKg").asDouble());
        if (!vehiculo.path("autonomiaKm").isNull()) {
            body.put("autonomiaKm", vehiculo.path("autonomiaKm").asInt());
        }
        body.put("estado", nuevoEstado);
        flota.put()
                .uri("/api/v1/vehiculos/{id}", vehiculo.path("id").asText())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public String buscarConductorPorVehiculo(String vehiculoId) {
        JsonNode arr = flota.get().uri("/api/v1/ruteo/conductores-disponibles").retrieve().body(JsonNode.class);
        if (arr == null || !arr.isArray()) {
            return null;
        }
        for (JsonNode c : arr) {
            if (vehiculoId.equals(c.path("vehiculoId").asText(null))) {
                return c.path("id").asText(null);
            }
        }
        return null;
    }
}

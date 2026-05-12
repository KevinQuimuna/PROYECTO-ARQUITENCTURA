package com.logiflow.taller.integration.flota;

import com.logiflow.taller.config.FlotaProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class FlotaRestClient {

    private final RestClient restClient;

    public FlotaRestClient(FlotaProperties props) {
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl().replaceAll("/$", ""))
                .build();
    }

    public VehiculoFlotaDto consultarPorMatricula(String matricula) {
        try {
            return restClient
                    .get()
                    .uri("/api/v1/vehiculos/matricula/{m}", matricula)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(VehiculoFlotaDto.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw e;
        }
    }

    public void actualizarVehiculo(Long id, VehiculoFlotaDto actualizado) {
        var body = new java.util.LinkedHashMap<String, Object>();
        body.put("matricula", actualizado.getMatricula());
        body.put("tipo", actualizado.getTipo());
        body.put("capacidadKg", actualizado.getCapacidadKg());
        body.put("autonomiaKm", actualizado.getAutonomiaKm());
        body.put("estado", actualizado.getEstado());
        restClient
                .put()
                .uri("/api/v1/vehiculos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}

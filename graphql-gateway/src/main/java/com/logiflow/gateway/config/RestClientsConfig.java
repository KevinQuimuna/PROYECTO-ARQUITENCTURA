package com.logiflow.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientsConfig {

    @Bean(name = "pedidosRestClient")
    public RestClient pedidos(@Value("${logiflow.pedidos-base-url}") String base) {
        return RestClient.builder().baseUrl(base).build();
    }

    @Bean(name = "ruteoRestClient")
    public RestClient ruteo(@Value("${logiflow.ruteo-base-url}") String base) {
        return RestClient.builder().baseUrl(base).build();
    }
}

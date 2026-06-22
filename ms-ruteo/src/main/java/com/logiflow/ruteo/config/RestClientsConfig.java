package com.logiflow.ruteo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientsConfig {

    @Bean(name = "flotaHttpRestClient")
    public RestClient flotaClient(@Value("${logiflow.flota-base-url}") String base) {
        return RestClient.builder().baseUrl(base).build();
    }

    @Bean(name = "pedidosHttpRestClient")
    public RestClient pedidosClient(@Value("${logiflow.pedidos-base-url}") String base) {
        return RestClient.builder().baseUrl(base).build();
    }
}

package com.logiflow.taller.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tallerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiFlow — ms-taller-rest (Anticorruption Layer)")
                        .version("1.0.0")
                        .description(
                                "Capa de traducción entre el dominio LogiFlow (Flota) y el contrato esperado por el taller mecánico externo."));
    }
}

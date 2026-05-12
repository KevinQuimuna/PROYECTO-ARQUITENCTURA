package com.logiflow.flota.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flotaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiFlow — ms-flota-rest")
                        .version("1.0.0")
                        .description("API REST del contexto Flota: vehículos, conductores y consultas para Ruteo.")
                        .contact(new Contact().name("LogiFlow").email("arquitectura@logiflow.local"))
                        .license(new License().name("Uso académico").url("https://logiflow.local")));
    }
}

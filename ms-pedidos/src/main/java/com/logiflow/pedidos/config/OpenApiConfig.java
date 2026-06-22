package com.logiflow.pedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-pedidos")
                        .description("Recepción y gestión de pedidos — publica eventos RabbitMQ")
                        .version("1.0"));
    }
}

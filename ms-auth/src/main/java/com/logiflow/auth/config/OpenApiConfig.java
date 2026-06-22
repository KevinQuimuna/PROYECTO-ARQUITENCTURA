package com.logiflow.auth.config;

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
                        .title("ms-auth")
                        .description("""
                                Autenticación JWT. Endpoints equivalentes:
                                - POST /login  =  POST /api/auth/login
                                - POST /verify =  POST /api/auth/verify
                                Usuario demo: admin / admin123
                                """)
                        .version("1.0"));
    }
}

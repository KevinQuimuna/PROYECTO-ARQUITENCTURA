package com.logiflow.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthDataLoader {

    @Bean
    CommandLineRunner seedAuth(AuthSeedService seedService) {
        return args -> seedService.seed();
    }
}

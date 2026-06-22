package com.logiflow.seguimiento;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SeguimientoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeguimientoApplication.class, args);
    }
}

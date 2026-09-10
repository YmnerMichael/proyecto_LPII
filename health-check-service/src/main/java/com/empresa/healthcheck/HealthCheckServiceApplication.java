package com.empresa.healthcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal. Punto de arranque de la aplicación Spring Boot.
 */
@SpringBootApplication
public class    HealthCheckServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthCheckServiceApplication.class, args);
    }

}

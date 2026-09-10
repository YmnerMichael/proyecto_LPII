package com.empresa.healthcheck;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que el contexto de Spring cargue correctamente,
 * confirmando que todas las dependencias y configuraciones son válidas.
 */
@SpringBootTest
class HealthCheckServiceApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto carga sin excepciones, la configuración es correcta.
    }
}

package com.empresa.healthcheck.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Health Check", description = "Verificación del estado de la aplicación")
public class HealthController {

    private final EntityManager entityManager;

    public HealthController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping("/health")
    @Operation(summary = "Verifica el estado de salud de la aplicación",
            description = "Retorna el estado general del servicio y de la conexión a la base de datos")
    public ResponseEntity<HealthResponseDTO> health() {

        String estadoBaseDatos;
        try {
            entityManager.createNativeQuery("SELECT 1 FROM DUAL").getSingleResult();
            estadoBaseDatos = "CONNECTED";
        } catch (Exception e) {
            estadoBaseDatos = "DISCONNECTED";
        }

        HealthResponseDTO respuesta = HealthResponseDTO.builder()
                .status("UP")
                .application("health-check-service")
                .version("1.0.0")
                .estadoBaseDatos(estadoBaseDatos)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    // El profe definió la clase directamente aquí adentro
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthResponseDTO {
        private String status;
        private String application;
        private String version;
        private String estadoBaseDatos;
    }
}

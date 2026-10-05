package com.empresa.healthcheck.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.empresa.healthcheck.dto.VentaRequestDTO;
import com.empresa.healthcheck.dto.VentaResponseDTO;
import com.empresa.healthcheck.enums.EstadoVenta;
import com.empresa.healthcheck.service.service.VentaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrar(
            @Valid
            @RequestBody VentaRequestDTO request) {

        VentaResponseDTO response = ventaService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ventaService.buscar(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listar() {

        return ResponseEntity.ok(
                ventaService.listar()
        );
    }

    /*
     * Búsqueda de ventas con filtros combinados.
     *
     * Todos los parámetros son opcionales; los que no se envían no
     * filtran. Sin coincidencias responde 200 con arreglo vacío.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<VentaResponseDTO>> buscar(

            @RequestParam(required = false)
            Long clienteId,

            @RequestParam(required = false)
            EstadoVenta estado,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(required = false, defaultValue = "fecha")
            String ordenarPor,

            @RequestParam(required = false, defaultValue = "desc")
            String direccion) {

        LocalDateTime desdeDateTime = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDateTime = (hasta != null) ? hasta.atTime(LocalTime.MAX) : null;

        return ResponseEntity.ok(
                ventaService.buscar(
                        clienteId,
                        estado,
                        desdeDateTime,
                        hastaDateTime,
                        ordenarPor,
                        direccion
                ));
    }
}
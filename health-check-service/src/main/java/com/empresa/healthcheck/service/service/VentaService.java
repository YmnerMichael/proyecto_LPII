package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.VentaRequestDTO;
import com.empresa.healthcheck.dto.VentaResponseDTO;
import com.empresa.healthcheck.enums.EstadoVenta;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    VentaResponseDTO registrar(VentaRequestDTO request);
    VentaResponseDTO buscar(Long id);
    List<VentaResponseDTO> listar();
    List<VentaResponseDTO> buscar(
            Long clienteId,
            EstadoVenta estado,
            LocalDateTime desde,
            LocalDateTime hasta,
            String ordenarPor,
            String direccion
    );
}

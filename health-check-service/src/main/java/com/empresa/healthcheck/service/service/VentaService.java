package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.VentaRequestDTO;
import com.empresa.healthcheck.dto.VentaResponseDTO;

import java.util.List;

public interface VentaService {
    VentaResponseDTO registrar(VentaRequestDTO request);
    VentaResponseDTO buscar(Long id);
    List<VentaResponseDTO> listar();
}

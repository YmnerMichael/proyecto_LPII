package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.reporte.ProductoMasVendidoDTO;
import com.empresa.healthcheck.dto.reporte.VentaPorCategoriaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<VentaPorCategoriaDTO> ventasPorCategoria(
            LocalDate desde,
            LocalDate hasta);

    List<ProductoMasVendidoDTO> productosMasVendidos(
            LocalDate desde,
            LocalDate hasta);
}

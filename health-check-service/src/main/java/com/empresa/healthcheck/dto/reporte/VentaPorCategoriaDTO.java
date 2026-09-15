package com.empresa.healthcheck.dto.reporte;

import java.math.BigDecimal;

public record VentaPorCategoriaDTO(Long categoriaId,
                                   String categoria,
                                   Long cantidad,
                                   BigDecimal total) {

}
package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.ProductoRequestDTO;
import com.empresa.healthcheck.dto.ProductoResponseDTO;
import com.empresa.healthcheck.service.generic.CrudService;

public interface ProductoService extends CrudService<ProductoRequestDTO, ProductoResponseDTO, Long> {
}

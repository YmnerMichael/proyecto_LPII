package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.CategoriaRequestDTO;
import com.empresa.healthcheck.dto.CategoriaResponseDTO;
import com.empresa.healthcheck.service.generic.CrudService;

public interface CategoriaService extends CrudService<CategoriaRequestDTO, CategoriaResponseDTO, Long> {
}

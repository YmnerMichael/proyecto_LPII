package com.empresa.healthcheck.service.service;

import com.empresa.healthcheck.dto.ClienteRequestDTO;
import com.empresa.healthcheck.dto.ClienteResponseDTO;
import com.empresa.healthcheck.service.generic.CrudService;

public interface ClienteService extends CrudService<ClienteRequestDTO, ClienteResponseDTO, Long> {
}

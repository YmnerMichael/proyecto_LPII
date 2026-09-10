package com.empresa.healthcheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.healthcheck.entity.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
}

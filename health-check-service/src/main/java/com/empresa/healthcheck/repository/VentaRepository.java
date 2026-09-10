package com.empresa.healthcheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.healthcheck.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}

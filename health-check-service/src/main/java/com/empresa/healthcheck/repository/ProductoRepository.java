package com.empresa.healthcheck.repository;

import com.empresa.healthcheck.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}

package com.empresa.healthcheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion; // <-- AGREGADO

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion; // <-- AGREGADO

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion; // <-- AGREGADO

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = true;
        }
        this.fechaCreacion = LocalDateTime.now(); // <-- Asigna fecha automáticamente al crear
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now(); // <-- Asigna fecha automáticamente al actualizar
    }
}

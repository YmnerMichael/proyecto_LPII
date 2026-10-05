package com.empresa.healthcheck.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.empresa.healthcheck.dto.ProductoRequestDTO;
import com.empresa.healthcheck.dto.ProductoResponseDTO;
import com.empresa.healthcheck.entity.Categoria;
import com.empresa.healthcheck.entity.Producto;
import com.empresa.healthcheck.exception.RecursosNoEncontradoException;
import com.empresa.healthcheck.exception.ReglaNegocioException;
import com.empresa.healthcheck.repository.CategoriaRepository;
import com.empresa.healthcheck.repository.ProductoRepository;
import com.empresa.healthcheck.service.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public ProductoResponseDTO create(ProductoRequestDTO t) {
        String nombre = t.getNombre().trim();
        if(productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un producto con el nombre: " + nombre);
        }

        Categoria categoria = categoriaRepository.findById(t.getCategoriaId()).orElseThrow(() ->
                new RecursosNoEncontradoException("Categoría no encontrada con el ID: " + t.getCategoriaId())
        );

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(t.getDescripcion());
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria); // Asignamos la relación

        Producto productoCreado = productoRepository.save(producto);
        return convertirResponse(productoCreado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO update(Long id, ProductoRequestDTO t) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradoException("Producto no encontrado con el ID: " + id)
        );

        Categoria categoria = categoriaRepository.findById(t.getCategoriaId()).orElseThrow(() ->
                new RecursosNoEncontradoException("Categoría no encontrada con el ID: " + t.getCategoriaId())
        );

        producto.setNombre(t.getNombre());
        producto.setDescripcion(t.getDescripcion());
        producto.setPrecio(t.getPrecio());
        producto.setStock(t.getStock());
        producto.setEstado(t.getEstado());
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);
        return convertirResponse(productoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO read(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradoException("Producto no encontrado con el ID: " + id)
        );
        return convertirResponse(producto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradoException("Producto no encontrado con el ID: " + id)
        );
        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll().stream()
                .map(this::convertirResponse)
                .toList();
    }

    private ProductoResponseDTO convertirResponse(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getEstado(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getFechaCreacion(),
                producto.getFechaModificacion()
        );
    }
}

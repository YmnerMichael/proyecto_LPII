package com.empresa.healthcheck.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.healthcheck.dto.CategoriaRequestDTO;
import com.empresa.healthcheck.dto.CategoriaResponseDTO;
import com.empresa.healthcheck.entity.Categoria;
import com.empresa.healthcheck.exception.RecursosNoEncontradoException;
import com.empresa.healthcheck.exception.ReglaNegocioException;
import com.empresa.healthcheck.repository.CategoriaRepository;
import com.empresa.healthcheck.service.service.CategoriaService;

import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public CategoriaResponseDTO create(CategoriaRequestDTO t) {
        String nombre = t.getNombre().trim(); // "Carnes " != "Carnes"
        if(categoriaRepository.existsByNombreIgnoreCase(nombre)){
            throw new ReglaNegocioException(
                    "Ya existe una categoria con el nomre "+ nombre
            );
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(t.getDescripcion());
        categoria.setEstado(t.getEstado());

        Categoria catCreada = categoriaRepository.save(categoria);

        return convertirResponse(catCreada);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO update(Long aLong, CategoriaRequestDTO t) {
        Categoria categoria = categoriaRepository.findById(aLong).orElseThrow(() ->
                new RecursosNoEncontradoException(
                        "Categoria no encontrada con id: "+ aLong
                )
        );

        categoria.setNombre(t.getNombre());
        categoria.setDescripcion(t.getDescripcion());
        categoria.setEstado(t.getEstado());

        Categoria catActualizada = categoriaRepository.save(categoria);

        return convertirResponse(catActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO read(Long aLong) {
        Categoria categoria = categoriaRepository.findById(aLong)
                .orElseThrow(() ->
                        new RecursosNoEncontradoException(
                                "Categoria no encontrada con id: "+ aLong
                        )
                );
        return convertirResponse(categoria);
    }

    @Override
    @Transactional
    public void delete(Long aLong) {
        Categoria categoria = categoriaRepository.findById(aLong).orElseThrow(() ->
                new RecursosNoEncontradoException(
                        "Categoria no encontrada con id: "+ aLong
                )
        );
        categoriaRepository.delete(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CategoriaResponseDTO> readAll() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private CategoriaResponseDTO convertirResponse(Categoria categoria){
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado(),
                categoria.getFechaCreacion(),
                categoria.getFechaModificacion()
        );
    }
}
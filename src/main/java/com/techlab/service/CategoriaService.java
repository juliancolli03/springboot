package com.techlab.service;

import com.techlab.exception.RecursoNoEncontradoException;
import com.techlab.model.Categoria;
import com.techlab.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
    }

    public Categoria buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con nombre: " + nombre));
    }

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria categoria = buscarPorId(id);
        
        // Verificar si el nombre ya existe en otra categoría
        if (!categoria.getNombre().equals(categoriaActualizada.getNombre()) 
            && categoriaRepository.existsByNombre(categoriaActualizada.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
        }
        
        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());
        
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}


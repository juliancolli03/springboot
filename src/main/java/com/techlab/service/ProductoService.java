package com.techlab.service;

import com.techlab.dto.ProductoDTO;
import com.techlab.exception.RecursoNoEncontradoException;
import com.techlab.exception.StockInsuficienteException;
import com.techlab.model.Categoria;
import com.techlab.model.Producto;
import com.techlab.repository.CategoriaRepository;
import com.techlab.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO crear(Producto producto) {
        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Categoría no encontrada con ID: " + producto.getCategoria().getId()));
        
        producto.setCategoria(categoria);
        Producto productoGuardado = productoRepository.save(producto);
        return convertirADTO(productoGuardado);
    }

    public ProductoDTO actualizar(Long id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));

        // Validar categoría si se proporciona
        if (productoActualizado.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(productoActualizado.getCategoria().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con ID: " + productoActualizado.getCategoria().getId()));
            producto.setCategoria(categoria);
        }

        // Actualizar campos
        if (productoActualizado.getNombre() != null) {
            producto.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getDescripcion() != null) {
            producto.setDescripcion(productoActualizado.getDescripcion());
        }
        if (productoActualizado.getPrecio() != null) {
            producto.setPrecio(productoActualizado.getPrecio());
        }
        if (productoActualizado.getImagenUrl() != null) {
            producto.setImagenUrl(productoActualizado.getImagenUrl());
        }
        if (productoActualizado.getStock() != null) {
            if (productoActualizado.getStock() < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
            producto.setStock(productoActualizado.getStock());
        }

        Producto productoGuardado = productoRepository.save(producto);
        return convertirADTO(productoGuardado);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
        productoRepository.delete(producto);
    }

    public void validarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + productoId));
        
        if (!producto.tieneStockSuficiente(cantidad)) {
            throw new StockInsuficienteException(
                "Stock insuficiente para el producto: " + producto.getNombre() + 
                ". Stock disponible: " + producto.getStock() + ", solicitado: " + cantidad);
        }
    }

    public List<ProductoDTO> productosConStockBajo(Integer stockMinimo) {
        return productoRepository.findByStockLessThan(stockMinimo).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getCategoria() != null ? producto.getCategoria().getId() : null,
            producto.getCategoria() != null ? producto.getCategoria().getNombre() : null,
            producto.getImagenUrl(),
            producto.getStock()
        );
    }
}


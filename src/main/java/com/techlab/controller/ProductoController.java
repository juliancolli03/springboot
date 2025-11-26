package com.techlab.controller;

import com.techlab.dto.ProductoDTO;
import com.techlab.model.Producto;
import com.techlab.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoDTO> productos = productoService.buscarPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody Producto producto) {
        ProductoDTO productoCreado = productoService.crear(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        ProductoDTO productoActualizado = productoService.actualizar(id, producto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> productosConStockBajo(@RequestParam(defaultValue = "10") Integer stockMinimo) {
        List<ProductoDTO> productos = productoService.productosConStockBajo(stockMinimo);
        return ResponseEntity.ok(productos);
    }
}


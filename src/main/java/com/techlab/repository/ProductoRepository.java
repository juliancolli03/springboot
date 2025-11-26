package com.techlab.repository;

import com.techlab.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);
    
    List<Producto> findByCategoriaId(Long categoriaId);
    
    List<Producto> findByStockLessThan(Integer stockMinimo);
}


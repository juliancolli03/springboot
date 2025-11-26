package com.techlab.repository;

import com.techlab.model.EstadoPedido;
import com.techlab.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByEstado(EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId ORDER BY p.fechaCreacion DESC")
    List<Pedido> findPedidosPorUsuarioOrdenadosPorFecha(@Param("usuarioId") Long usuarioId);
}


package com.techlab.service;

import com.techlab.dto.CrearPedidoDTO;
import com.techlab.exception.RecursoNoEncontradoException;
import com.techlab.exception.StockInsuficienteException;
import com.techlab.model.*;
import com.techlab.repository.PedidoRepository;
import com.techlab.repository.ProductoRepository;
import com.techlab.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> buscarPorUsuario(Long usuarioId) {
        return pedidoRepository.findPedidosPorUsuarioOrdenadosPorFecha(usuarioId);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido crear(CrearPedidoDTO crearPedidoDTO) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(crearPedidoDTO.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Usuario no encontrado con ID: " + crearPedidoDTO.getUsuarioId()));

        // Crear el pedido
        Pedido pedido = new Pedido(usuario);

        // Validar y agregar líneas de pedido
        for (CrearPedidoDTO.LineaPedidoDTO lineaDTO : crearPedidoDTO.getLineasPedido()) {
            // Validar que el producto existe
            Producto producto = productoRepository.findById(lineaDTO.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + lineaDTO.getProductoId()));

            // Validar stock
            if (!producto.tieneStockSuficiente(lineaDTO.getCantidad())) {
                throw new StockInsuficienteException(
                    "Stock insuficiente para el producto: " + producto.getNombre() + 
                    ". Stock disponible: " + producto.getStock() + ", solicitado: " + lineaDTO.getCantidad());
            }

            // Crear línea de pedido
            LineaPedido lineaPedido = new LineaPedido();
            lineaPedido.setProducto(producto);
            lineaPedido.setCantidad(lineaDTO.getCantidad());
            lineaPedido.setPrecioUnitario(producto.getPrecio());

            pedido.agregarLineaPedido(lineaPedido);
        }

        // Guardar el pedido (sin confirmar aún)
        return pedidoRepository.save(pedido);
    }

    public Pedido confirmar(Long id) {
        Pedido pedido = buscarPorId(id);
        pedido.confirmar(); // Este método valida stock y actualiza
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(id);
        
        // Validaciones de transición de estado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException(
                "No se puede cambiar el estado de un pedido " + pedido.getEstado().name().toLowerCase());
        }
        
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        Pedido pedido = buscarPorId(id);
        
        // Solo se pueden eliminar pedidos pendientes
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException(
                "Solo se pueden eliminar pedidos en estado PENDIENTE. Estado actual: " + pedido.getEstado());
        }
        
        pedidoRepository.delete(pedido);
    }
}


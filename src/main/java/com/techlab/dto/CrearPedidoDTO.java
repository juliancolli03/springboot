package com.techlab.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearPedidoDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "Las líneas de pedido son obligatorias")
    private List<LineaPedidoDTO> lineasPedido;

    // Constructores
    public CrearPedidoDTO() {
    }

    public CrearPedidoDTO(Long usuarioId, List<LineaPedidoDTO> lineasPedido) {
        this.usuarioId = usuarioId;
        this.lineasPedido = lineasPedido;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<LineaPedidoDTO> getLineasPedido() {
        return lineasPedido;
    }

    public void setLineasPedido(List<LineaPedidoDTO> lineasPedido) {
        this.lineasPedido = lineasPedido;
    }

    // Clase interna para las líneas de pedido
    public static class LineaPedidoDTO {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        public LineaPedidoDTO() {
        }

        public LineaPedidoDTO(Long productoId, Integer cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}


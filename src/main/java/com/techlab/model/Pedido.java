package com.techlab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineaPedido> lineasPedido = new ArrayList<>();

    @NotNull(message = "El estado del pedido es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructores
    public Pedido() {
        this.estado = EstadoPedido.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public Pedido(Usuario usuario) {
        this();
        this.usuario = usuario;
    }

    // Método para calcular el total del pedido
    public BigDecimal calcularTotal() {
        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (LineaPedido linea : lineasPedido) {
            totalCalculado = totalCalculado.add(linea.calcularSubtotal());
        }
        this.total = totalCalculado;
        return totalCalculado;
    }

    // Método para agregar una línea de pedido
    public void agregarLineaPedido(LineaPedido lineaPedido) {
        lineasPedido.add(lineaPedido);
        lineaPedido.setPedido(this);
        calcularTotal();
    }

    // Método para remover una línea de pedido
    public void removerLineaPedido(LineaPedido lineaPedido) {
        lineasPedido.remove(lineaPedido);
        lineaPedido.setPedido(null);
        calcularTotal();
    }

    // Método para confirmar el pedido y actualizar stock
    public void confirmar() {
        if (this.estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos pendientes");
        }
        
        // Validar stock antes de confirmar
        for (LineaPedido linea : lineasPedido) {
            Producto producto = linea.getProducto();
            if (!producto.tieneStockSuficiente(linea.getCantidad())) {
                throw new IllegalStateException(
                    "Stock insuficiente para el producto: " + producto.getNombre()
                );
            }
        }
        
        // Disminuir stock
        for (LineaPedido linea : lineasPedido) {
            linea.getProducto().disminuirStock(linea.getCantidad());
        }
        
        this.estado = EstadoPedido.CONFIRMADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<LineaPedido> getLineasPedido() {
        return lineasPedido;
    }

    public void setLineasPedido(List<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
        calcularTotal();
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        calcularTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        calcularTotal();
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getNombre() : "null") +
                ", estado=" + estado +
                ", total=" + total +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}


package com.techlab.controller;

import com.techlab.dto.CrearPedidoDTO;
import com.techlab.model.EstadoPedido;
import com.techlab.model.Pedido;
import com.techlab.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> buscarPorEstado(@PathVariable EstadoPedido estado) {
        List<Pedido> pedidos = pedidoService.buscarPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody CrearPedidoDTO crearPedidoDTO) {
        Pedido pedido = pedidoService.crear(crearPedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Pedido> confirmar(@PathVariable Long id) {
        Pedido pedido = pedidoService.confirmar(id);
        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestBody EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}


package com.inovatech.ms_pedidos_innovatech.controller;

import com.inovatech.ms_pedidos_innovatech.dto.request.ActualizarEstadoPedidoRequest;
import com.inovatech.ms_pedidos_innovatech.dto.request.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.dto.response.PedidoResponse;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedidoService.crearPedido(request)));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> obtenerTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.obtenerTodosLosPedidos().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        return ResponseEntity.ok(toResponse(pedidoService.obtenerPedidoPorId(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorCliente(clienteId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorEstado(estado).stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request) {
        return ResponseEntity.ok(toResponse(pedidoService.actualizarEstado(id, request.estado())));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(toResponse(pedidoService.obtenerPedidoPorId(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    private PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getProducto(),
                pedido.getPrecio(),
                pedido.getEstado(),
                pedido.getFechaCreacion());
    }
}

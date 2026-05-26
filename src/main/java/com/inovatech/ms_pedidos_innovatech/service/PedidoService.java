package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.dto.request.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.exception.BusinessException;
import com.inovatech.ms_pedidos_innovatech.exception.ResourceNotFoundException;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.repository.PedidoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public Pedido crearPedido(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(request.clienteId());
        pedido.setProducto(request.producto());
        pedido.setPrecio(request.precio());
        pedido.setEstado(request.estado());
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> obtenerPedidosPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedidoPorId(id);
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public void cancelarPedido(Long id) {
        Pedido pedido = obtenerPedidoPorId(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        Pedido pedido = obtenerPedidoPorId(id);
        pedidoRepository.delete(pedido);
    }
}

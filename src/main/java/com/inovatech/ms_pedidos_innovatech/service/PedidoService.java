package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Crear un nuevo pedido
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Obtener todos los pedidos
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    // Obtener pedido por ID
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // Obtener pedidos por cliente
    public List<Pedido> obtenerPedidosPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    // Obtener pedidos por estado
    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // Actualizar estado de un pedido
    public Optional<Pedido> actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            p.setEstado(nuevoEstado);
            return Optional.of(pedidoRepository.save(p));
        }
        return Optional.empty();
    }

    // Cancelar un pedido
    public boolean cancelarPedido(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent() && pedido.get().getEstado() == EstadoPedido.PENDIENTE) {
            Pedido p = pedido.get();
            p.setEstado(EstadoPedido.CANCELADO);
            pedidoRepository.save(p);
            return true;
        }
        return false;
    }

    // Eliminar un pedido
    public boolean eliminarPedido(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

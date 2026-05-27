package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.dto.request.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.exception.BusinessException;
import com.inovatech.ms_pedidos_innovatech.exception.ResourceNotFoundException;
import com.inovatech.ms_pedidos_innovatech.messaging.PedidoEventPublisher;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.repository.PedidoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher pedidoEventPublisher;

    @Transactional
    public Pedido crearPedido(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(request.clienteId());
        pedido.setProducto(request.producto());
        pedido.setPrecio(request.precio());
        pedido.setEstado(request.estado());
        pedido.setNombreDestinatario(request.nombreDestinatario());
        pedido.setDireccionDestino(request.direccionDestino());
        pedido.setCiudadDestino(request.ciudadDestino());
        pedido.setRegionDestino(request.regionDestino());
        pedido.setTelefonoContacto(request.telefonoContacto());
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

    @Transactional
    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedidoPorId(id);
        EstadoPedido estadoAnterior = pedido.getEstado();
        if (estadoAnterior != EstadoPedido.PAGADO && nuevoEstado == EstadoPedido.PAGADO) {
            validarDatosParaPago(pedido);
        }
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        if (estadoAnterior != EstadoPedido.PAGADO && nuevoEstado == EstadoPedido.PAGADO) {
            pedidoEventPublisher.publicarPedidoPagado(pedidoActualizado);
        }
        return pedidoActualizado;
    }

    @Transactional
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

    private void validarDatosParaPago(Pedido pedido) {
        try {
            Long.parseLong(pedido.getClienteId());
        } catch (NumberFormatException ex) {
            throw new BusinessException("El clienteId debe ser numerico para publicar el evento Pedido_Pagado");
        }

        if (esBlank(pedido.getNombreDestinatario())
                || esBlank(pedido.getDireccionDestino())
                || esBlank(pedido.getCiudadDestino())) {
            throw new BusinessException(
                    "El pedido requiere nombreDestinatario, direccionDestino y ciudadDestino antes de pasar a PAGADO");
        }
    }

    private boolean esBlank(String value) {
        return value == null || value.isBlank();
    }
}

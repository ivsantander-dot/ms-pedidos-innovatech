package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.dto.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.repository.PedidoRepository;
import com.inovatech.ms_pedidos_innovatech.security.AuthenticatedUser;
import com.inovatech.ms_pedidos_innovatech.security.SecurityUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public Pedido crearPedido(PedidoRequest request, AuthenticatedUser currentUser) {
        String resolvedClienteId = resolveTargetClienteId(request.getClienteId(), currentUser);
        Pedido pedido = new Pedido();
        pedido.setClienteId(resolvedClienteId);
        pedido.setProducto(request.getProducto());
        pedido.setPrecio(request.getPrecio());
        pedido.setEstado(parseEstado(request.getEstado()));
        pedido.setNombreDestinatario(request.getNombreDestinatario());
        pedido.setDireccionDestino(request.getDireccionDestino());
        pedido.setCiudadDestino(request.getCiudadDestino());
        pedido.setRegionDestino(request.getRegionDestino());
        pedido.setTelefonoContacto(request.getTelefonoContacto());
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPedidoPorId(Long id, AuthenticatedUser currentUser) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        pedido.ifPresent(value -> securityUtils.requireOwnerOrAdmin(currentUser, value.getClienteId()));
        return pedido;
    }

    public List<Pedido> obtenerPedidosPorCliente(String clienteId, AuthenticatedUser currentUser) {
        securityUtils.requireOwnerOrAdmin(currentUser, clienteId);
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> obtenerMisPedidos(AuthenticatedUser currentUser) {
        return pedidoRepository.findByClienteId(String.valueOf(currentUser.userId()));
    }

    public List<Pedido> obtenerPedidosPorFecha(String fecha) {
        if ("hoy".equalsIgnoreCase(fecha)) {
            LocalDate hoy = LocalDate.now();
            LocalDateTime inicio = hoy.atStartOfDay();
            LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
            return pedidoRepository.findByFechaCreacionBetween(inicio, fin);
        }

        LocalDate dia = LocalDate.parse(fecha);
        return pedidoRepository.findByFechaCreacionBetween(dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
    }

    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Optional<Pedido> actualizarEstado(Long id, EstadoPedido nuevoEstado, AuthenticatedUser currentUser) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido value = pedido.get();
            if (!currentUser.hasRole("ADMIN")) {
                securityUtils.requireOwnerOrAdmin(currentUser, value.getClienteId());
            }
            value.setEstado(nuevoEstado);
            return Optional.of(pedidoRepository.save(value));
        }
        return Optional.empty();
    }

    public boolean cancelarPedido(Long id, AuthenticatedUser currentUser) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent() && pedido.get().getEstado() == EstadoPedido.PENDIENTE) {
            Pedido value = pedido.get();
            securityUtils.requireOwnerOrAdmin(currentUser, value.getClienteId());
            value.setEstado(EstadoPedido.CANCELADO);
            pedidoRepository.save(value);
            return true;
        }
        return false;
    }

    public boolean eliminarPedido(Long id, AuthenticatedUser currentUser) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            if (!currentUser.hasRole("ADMIN")) {
                securityUtils.requireOwnerOrAdmin(currentUser, pedido.get().getClienteId());
            }
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private String resolveTargetClienteId(String requestClienteId, AuthenticatedUser currentUser) {
        String authenticatedClienteId = String.valueOf(currentUser.userId());

        if (currentUser.hasRole("ADMIN")) {
            if (requestClienteId == null || requestClienteId.isBlank()) {
                throw new IllegalArgumentException("clienteId es obligatorio para operaciones administrativas");
            }
            return requestClienteId.trim();
        }

        if (requestClienteId != null && !requestClienteId.isBlank() && !Objects.equals(requestClienteId.trim(), authenticatedClienteId)) {
            System.out.printf("WARN pedido.create request clienteId=%s ignored, authenticated userId=%s%n", requestClienteId, authenticatedClienteId);
        }

        return authenticatedClienteId;
    }

    private EstadoPedido parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return EstadoPedido.PENDIENTE;
        }
        return EstadoPedido.valueOf(estado.trim().toUpperCase());
    }
}

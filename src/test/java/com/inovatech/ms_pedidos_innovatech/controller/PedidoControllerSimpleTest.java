package com.inovatech.ms_pedidos_innovatech.controller;

import com.inovatech.ms_pedidos_innovatech.dto.request.ActualizarEstadoPedidoRequest;
import com.inovatech.ms_pedidos_innovatech.dto.request.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.dto.response.PedidoResponse;
import com.inovatech.ms_pedidos_innovatech.exception.BusinessException;
import com.inovatech.ms_pedidos_innovatech.exception.ResourceNotFoundException;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerSimpleTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedido1;
    private Pedido pedido2;
    private PedidoRequest request1;

    @BeforeEach
    void setUp() {
        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setClienteId("cliente123");
        pedido1.setProducto("Laptop");
        pedido1.setPrecio(1200.0);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setClienteId("cliente456");
        pedido2.setProducto("Mouse");
        pedido2.setPrecio(25.0);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        request1 = new PedidoRequest("cliente123", "Laptop", 1200.0, EstadoPedido.PENDIENTE, null, null, null, null, null);
    }

    @Test
    void crearPedido_DebeRetornarPedidoCreado() {
        when(pedidoService.crearPedido(any(PedidoRequest.class))).thenReturn(pedido1);

        ResponseEntity<PedidoResponse> response = pedidoController.crearPedido(request1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("cliente123", response.getBody().clienteId());
        verify(pedidoService, times(1)).crearPedido(any(PedidoRequest.class));
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaDePedidos() {
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoService.obtenerTodosLosPedidos()).thenReturn(pedidos);

        ResponseEntity<List<PedidoResponse>> response = pedidoController.obtenerTodosLosPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(pedidoService, times(1)).obtenerTodosLosPedidos();
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaVacia() {
        when(pedidoService.obtenerTodosLosPedidos()).thenReturn(Arrays.asList());

        ResponseEntity<List<PedidoResponse>> response = pedidoController.obtenerTodosLosPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(pedidoService, times(1)).obtenerTodosLosPedidos();
    }

    @Test
    void obtenerPedidoPorId_DebeRetornarPedidoCuandoExiste() {
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(pedido1);

        ResponseEntity<PedidoResponse> response = pedidoController.obtenerPedidoPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        verify(pedidoService, times(1)).obtenerPedidoPorId(1L);
    }

    @Test
    void obtenerPedidoPorId_DebeLanzarExcepcionCuandoNoExiste() {
        when(pedidoService.obtenerPedidoPorId(999L))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado con ID: 999"));

        assertThrows(ResourceNotFoundException.class, () -> pedidoController.obtenerPedidoPorId(999L));
        verify(pedidoService, times(1)).obtenerPedidoPorId(999L);
    }

    @Test
    void obtenerPedidosPorCliente_DebeRetornarPedidosDelCliente() {
        List<Pedido> pedidosCliente = Arrays.asList(pedido1);
        when(pedidoService.obtenerPedidosPorCliente("cliente123")).thenReturn(pedidosCliente);

        ResponseEntity<List<PedidoResponse>> response = pedidoController.obtenerPedidosPorCliente("cliente123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("cliente123", response.getBody().get(0).clienteId());
        verify(pedidoService, times(1)).obtenerPedidosPorCliente("cliente123");
    }

    @Test
    void obtenerPedidosPorEstado_DebeRetornarPedidosConEstado() {
        List<Pedido> pedidosPendientes = Arrays.asList(pedido1);
        when(pedidoService.obtenerPedidosPorEstado(EstadoPedido.PENDIENTE)).thenReturn(pedidosPendientes);

        ResponseEntity<List<PedidoResponse>> response = pedidoController.obtenerPedidosPorEstado(EstadoPedido.PENDIENTE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(EstadoPedido.PENDIENTE, response.getBody().get(0).estado());
        verify(pedidoService, times(1)).obtenerPedidosPorEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    void actualizarEstado_DebeRetornarPedidoActualizado() {
        ActualizarEstadoPedidoRequest estadoRequest = new ActualizarEstadoPedidoRequest(EstadoPedido.ENVIADO);
        pedido1.setEstado(EstadoPedido.ENVIADO);
        when(pedidoService.actualizarEstado(1L, EstadoPedido.ENVIADO)).thenReturn(pedido1);

        ResponseEntity<PedidoResponse> response = pedidoController.actualizarEstado(1L, estadoRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EstadoPedido.ENVIADO, response.getBody().estado());
        verify(pedidoService, times(1)).actualizarEstado(1L, EstadoPedido.ENVIADO);
    }

    @Test
    void actualizarEstado_DebeLanzarExcepcionCuandoNoExiste() {
        ActualizarEstadoPedidoRequest estadoRequest = new ActualizarEstadoPedidoRequest(EstadoPedido.ENVIADO);
        when(pedidoService.actualizarEstado(999L, EstadoPedido.ENVIADO))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> pedidoController.actualizarEstado(999L, estadoRequest));
        verify(pedidoService, times(1)).actualizarEstado(999L, EstadoPedido.ENVIADO);
    }

    @Test
    void cancelarPedido_DebeRetornarPedidoCancelado() {
        pedido1.setEstado(EstadoPedido.CANCELADO);
        doNothing().when(pedidoService).cancelarPedido(1L);
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(pedido1);

        ResponseEntity<PedidoResponse> response = pedidoController.cancelarPedido(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EstadoPedido.CANCELADO, response.getBody().estado());
        verify(pedidoService, times(1)).cancelarPedido(1L);
        verify(pedidoService, times(1)).obtenerPedidoPorId(1L);
    }

    @Test
    void cancelarPedido_DebeLanzarExcepcionCuandoNoPuedeCancelarse() {
        doThrow(new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE"))
                .when(pedidoService).cancelarPedido(1L);

        assertThrows(BusinessException.class, () -> pedidoController.cancelarPedido(1L));
        verify(pedidoService, times(1)).cancelarPedido(1L);
    }

    @Test
    void eliminarPedido_DebeRetornarNoContent() {
        doNothing().when(pedidoService).eliminarPedido(1L);

        ResponseEntity<Void> response = pedidoController.eliminarPedido(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoService, times(1)).eliminarPedido(1L);
    }

    @Test
    void eliminarPedido_DebeLanzarExcepcionCuandoNoExiste() {
        doThrow(new ResourceNotFoundException("Pedido no encontrado con ID: 999"))
                .when(pedidoService).eliminarPedido(999L);

        assertThrows(ResourceNotFoundException.class, () -> pedidoController.eliminarPedido(999L));
        verify(pedidoService, times(1)).eliminarPedido(999L);
    }
}

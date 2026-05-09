package com.inovatech.ms_pedidos_innovatech.controller;

import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    }

    @Test
    void crearPedido_DebeRetornarPedidoCreado() {
        when(pedidoService.crearPedido(any(Pedido.class))).thenReturn(pedido1);

        Pedido resultado = pedidoController.crearPedido(pedido1);

        assertNotNull(resultado);
        assertEquals(pedido1.getId(), resultado.getId());
        assertEquals(pedido1.getClienteId(), resultado.getClienteId());
        assertEquals(pedido1.getProducto(), resultado.getProducto());
        assertEquals(pedido1.getPrecio(), resultado.getPrecio());
        assertEquals(pedido1.getEstado(), resultado.getEstado());

        verify(pedidoService, times(1)).crearPedido(pedido1);
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaDePedidos() {
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoService.obtenerTodosLosPedidos()).thenReturn(pedidos);

        List<Pedido> resultado = pedidoController.obtenerTodosLosPedidos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(pedido1));
        assertTrue(resultado.contains(pedido2));

        verify(pedidoService, times(1)).obtenerTodosLosPedidos();
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaVacia() {
        when(pedidoService.obtenerTodosLosPedidos()).thenReturn(Arrays.asList());

        List<Pedido> resultado = pedidoController.obtenerTodosLosPedidos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(pedidoService, times(1)).obtenerTodosLosPedidos();
    }

    @Test
    void obtenerPedidoPorId_DebeRetornarPedidoCuandoExiste() {
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido1));

        var resultado = pedidoController.obtenerPedidoPorId(1L);

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertEquals(pedido1, resultado.getBody());

        verify(pedidoService, times(1)).obtenerPedidoPorId(1L);
    }

    @Test
    void obtenerPedidoPorId_DebeRetornarNotFoundCuandoNoExiste() {
        when(pedidoService.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());

        var resultado = pedidoController.obtenerPedidoPorId(999L);

        assertTrue(resultado.getStatusCode().is4xxClientError());

        verify(pedidoService, times(1)).obtenerPedidoPorId(999L);
    }

    @Test
    void obtenerPedidosPorCliente_DebeRetornarPedidosDelCliente() {
        List<Pedido> pedidosCliente = Arrays.asList(pedido1);
        when(pedidoService.obtenerPedidosPorCliente("cliente123")).thenReturn(pedidosCliente);

        List<Pedido> resultado = pedidoController.obtenerPedidosPorCliente("cliente123");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("cliente123", resultado.get(0).getClienteId());

        verify(pedidoService, times(1)).obtenerPedidosPorCliente("cliente123");
    }

    @Test
    void obtenerPedidosPorEstado_DebeRetornarPedidosConEstado() {
        List<Pedido> pedidosPendientes = Arrays.asList(pedido1);
        when(pedidoService.obtenerPedidosPorEstado(EstadoPedido.PENDIENTE)).thenReturn(pedidosPendientes);

        List<Pedido> resultado = pedidoController.obtenerPedidosPorEstado(EstadoPedido.PENDIENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EstadoPedido.PENDIENTE, resultado.get(0).getEstado());

        verify(pedidoService, times(1)).obtenerPedidosPorEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    void actualizarEstado_DebeRetornarPedidoActualizadoCuandoExiste() {
        pedido1.setEstado(EstadoPedido.ENVIADO);
        when(pedidoService.actualizarEstado(1L, EstadoPedido.ENVIADO)).thenReturn(Optional.of(pedido1));

        var resultado = pedidoController.actualizarEstado(1L, EstadoPedido.ENVIADO);

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertEquals(EstadoPedido.ENVIADO, resultado.getBody().getEstado());

        verify(pedidoService, times(1)).actualizarEstado(1L, EstadoPedido.ENVIADO);
    }

    @Test
    void actualizarEstado_DebeRetornarNotFoundCuandoNoExiste() {
        when(pedidoService.actualizarEstado(999L, EstadoPedido.ENVIADO)).thenReturn(Optional.empty());

        var resultado = pedidoController.actualizarEstado(999L, EstadoPedido.ENVIADO);

        assertTrue(resultado.getStatusCode().is4xxClientError());

        verify(pedidoService, times(1)).actualizarEstado(999L, EstadoPedido.ENVIADO);
    }

    @Test
    void cancelarPedido_DebeRetornarMensajeExitoCuandoSePuedeCancelar() {
        when(pedidoService.cancelarPedido(1L)).thenReturn(true);

        var resultado = pedidoController.cancelarPedido(1L);

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertEquals("Pedido cancelado exitosamente", resultado.getBody());

        verify(pedidoService, times(1)).cancelarPedido(1L);
    }

    @Test
    void cancelarPedido_DebeRetornarBadRequestCuandoNoSePuedeCancelar() {
        when(pedidoService.cancelarPedido(1L)).thenReturn(false);

        var resultado = pedidoController.cancelarPedido(1L);

        assertTrue(resultado.getStatusCode().is4xxClientError());
        assertEquals("No se pudo cancelar el pedido. Verifique que exista y esté en estado PENDIENTE.", resultado.getBody());

        verify(pedidoService, times(1)).cancelarPedido(1L);
    }

    @Test
    void eliminarPedido_DebeRetornarMensajeExitoCuandoExiste() {
        when(pedidoService.eliminarPedido(1L)).thenReturn(true);

        var resultado = pedidoController.eliminarPedido(1L);

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertEquals("Pedido eliminado exitosamente", resultado.getBody());

        verify(pedidoService, times(1)).eliminarPedido(1L);
    }

    @Test
    void eliminarPedido_DebeRetornarNotFoundCuandoNoExiste() {
        when(pedidoService.eliminarPedido(999L)).thenReturn(false);

        var resultado = pedidoController.eliminarPedido(999L);

        assertTrue(resultado.getStatusCode().is4xxClientError());

        verify(pedidoService, times(1)).eliminarPedido(999L);
    }
}

package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.dto.request.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.exception.BusinessException;
import com.inovatech.ms_pedidos_innovatech.exception.ResourceNotFoundException;
import com.inovatech.ms_pedidos_innovatech.messaging.PedidoEventPublisher;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.repository.PedidoRepository;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEventPublisher pedidoEventPublisher;

    @InjectMocks
    private PedidoService pedidoService;

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
    void crearPedido_DebeRetornarPedidoGuardado() {
        PedidoRequest request = new PedidoRequest("cliente123", "Laptop", 1200.0, EstadoPedido.PENDIENTE, null, null, null, null, null);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido1);

        Pedido resultado = pedidoService.crearPedido(request);

        assertNotNull(resultado);
        assertEquals(pedido1.getId(), resultado.getId());
        assertEquals(pedido1.getClienteId(), resultado.getClienteId());
        assertEquals(pedido1.getProducto(), resultado.getProducto());
        assertEquals(pedido1.getPrecio(), resultado.getPrecio());
        assertEquals(pedido1.getEstado(), resultado.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void crearPedido_ConPedidoNuevo_DebeAsignarValoresPorDefecto() {
        PedidoRequest request = new PedidoRequest("cliente789", "Teclado", 50.0, null, null, null, null, null, null);

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(3L);
        pedidoGuardado.setClienteId("cliente789");
        pedidoGuardado.setProducto("Teclado");
        pedidoGuardado.setPrecio(50.0);
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);
        pedidoGuardado.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        Pedido resultado = pedidoService.crearPedido(request);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("cliente789", resultado.getClienteId());
        assertEquals("Teclado", resultado.getProducto());
        assertEquals(50.0, resultado.getPrecio());
        assertNotNull(resultado.getFechaCreacion());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaDePedidos() {
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<Pedido> resultado = pedidoService.obtenerTodosLosPedidos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(pedido1));
        assertTrue(resultado.contains(pedido2));
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodosLosPedidos_DebeRetornarListaVacia() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList());

        List<Pedido> resultado = pedidoService.obtenerTodosLosPedidos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPedidoPorId_DebeRetornarPedidoCuandoExiste() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));

        Pedido resultado = pedidoService.obtenerPedidoPorId(1L);

        assertNotNull(resultado);
        assertEquals(pedido1, resultado);
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPedidoPorId_DebeLanzarExcepcionCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.obtenerPedidoPorId(999L));
        verify(pedidoRepository, times(1)).findById(999L);
    }

    @Test
    void obtenerPedidosPorCliente_DebeRetornarPedidosDelCliente() {
        List<Pedido> pedidosCliente = Arrays.asList(pedido1);
        when(pedidoRepository.findByClienteId("cliente123")).thenReturn(pedidosCliente);

        List<Pedido> resultado = pedidoService.obtenerPedidosPorCliente("cliente123");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("cliente123", resultado.get(0).getClienteId());
        verify(pedidoRepository, times(1)).findByClienteId("cliente123");
    }

    @Test
    void obtenerPedidosPorCliente_DebeRetornarListaVaciaCuandoNoHayPedidos() {
        when(pedidoRepository.findByClienteId("clienteInexistente")).thenReturn(Arrays.asList());

        List<Pedido> resultado = pedidoService.obtenerPedidosPorCliente("clienteInexistente");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoRepository, times(1)).findByClienteId("clienteInexistente");
    }

    @Test
    void obtenerPedidosPorEstado_DebeRetornarPedidosConEstado() {
        List<Pedido> pedidosPendientes = Arrays.asList(pedido1);
        when(pedidoRepository.findByEstado(EstadoPedido.PENDIENTE)).thenReturn(pedidosPendientes);

        List<Pedido> resultado = pedidoService.obtenerPedidosPorEstado(EstadoPedido.PENDIENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EstadoPedido.PENDIENTE, resultado.get(0).getEstado());
        verify(pedidoRepository, times(1)).findByEstado(EstadoPedido.PENDIENTE);
    }

    @Test
    void obtenerPedidosPorEstado_DebeRetornarListaVaciaCuandoNoHayPedidosConEstado() {
        when(pedidoRepository.findByEstado(EstadoPedido.CANCELADO)).thenReturn(Arrays.asList());

        List<Pedido> resultado = pedidoService.obtenerPedidosPorEstado(EstadoPedido.CANCELADO);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoRepository, times(1)).findByEstado(EstadoPedido.CANCELADO);
    }

    @Test
    void actualizarEstado_DebeRetornarPedidoActualizadoCuandoExiste() {
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1L);
        pedidoActualizado.setClienteId("cliente123");
        pedidoActualizado.setProducto("Laptop");
        pedidoActualizado.setPrecio(1200.0);
        pedidoActualizado.setEstado(EstadoPedido.ENVIADO);
        pedidoActualizado.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoActualizado);

        Pedido resultado = pedidoService.actualizarEstado(1L, EstadoPedido.ENVIADO);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.ENVIADO, resultado.getEstado());
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido1);
    }

    @Test
    void actualizarEstado_DebeLanzarExcepcionCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.actualizarEstado(999L, EstadoPedido.ENVIADO));
        verify(pedidoRepository, times(1)).findById(999L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeCompletarseCuandoExisteYEstaPendiente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido1);

        assertDoesNotThrow(() -> pedidoService.cancelarPedido(1L));
        assertEquals(EstadoPedido.CANCELADO, pedido1.getEstado());
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido1);
    }

    @Test
    void cancelarPedido_DebeLanzarExcepcionCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.cancelarPedido(999L));
        verify(pedidoRepository, times(1)).findById(999L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeLanzarExcepcionCuandoNoEstaPendiente() {
        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedido2));

        assertThrows(BusinessException.class, () -> pedidoService.cancelarPedido(2L));
        assertEquals(EstadoPedido.ENVIADO, pedido2.getEstado());
        verify(pedidoRepository, times(1)).findById(2L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeLanzarExcepcionCuandoYaEstaCancelado() {
        pedido1.setEstado(EstadoPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));

        assertThrows(BusinessException.class, () -> pedidoService.cancelarPedido(1L));
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void eliminarPedido_DebeCompletarseCuandoExiste() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));

        assertDoesNotThrow(() -> pedidoService.eliminarPedido(1L));
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).delete(pedido1);
    }

    @Test
    void eliminarPedido_DebeLanzarExcepcionCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.eliminarPedido(999L));
        verify(pedidoRepository, times(1)).findById(999L);
        verify(pedidoRepository, never()).delete(any(Pedido.class));
    }

    @Test
    void actualizarEstado_DebeManejarTodosLosEstados() {
        Pedido pedidoBase = new Pedido();
        pedidoBase.setId(1L);
        pedidoBase.setClienteId("cliente123");
        pedidoBase.setProducto("Laptop");
        pedidoBase.setPrecio(1200.0);
        pedidoBase.setEstado(EstadoPedido.PENDIENTE);
        pedidoBase.setFechaCreacion(LocalDateTime.now());

        EstadoPedido[] estados = {EstadoPedido.PENDIENTE, EstadoPedido.ENVIADO, EstadoPedido.RECIBIDO, EstadoPedido.CANCELADO};

        for (EstadoPedido estado : estados) {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoBase);

            Pedido resultado = pedidoService.actualizarEstado(1L, estado);

            assertNotNull(resultado);
            verify(pedidoRepository, times(1)).findById(1L);
            verify(pedidoRepository, times(1)).save(pedidoBase);

            reset(pedidoRepository);
        }
    }
}

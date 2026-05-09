package com.inovatech.ms_pedidos_innovatech.service;

import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
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
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido1);

        Pedido resultado = pedidoService.crearPedido(pedido1);

        assertNotNull(resultado);
        assertEquals(pedido1.getId(), resultado.getId());
        assertEquals(pedido1.getClienteId(), resultado.getClienteId());
        assertEquals(pedido1.getProducto(), resultado.getProducto());
        assertEquals(pedido1.getPrecio(), resultado.getPrecio());
        assertEquals(pedido1.getEstado(), resultado.getEstado());

        verify(pedidoRepository, times(1)).save(pedido1);
    }

    @Test
    void crearPedido_ConPedidoNuevo_DebeAsignarValoresPorDefecto() {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setClienteId("cliente789");
        nuevoPedido.setProducto("Teclado");
        nuevoPedido.setPrecio(50.0);

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(3L);
        pedidoGuardado.setClienteId("cliente789");
        pedidoGuardado.setProducto("Teclado");
        pedidoGuardado.setPrecio(50.0);
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);
        pedidoGuardado.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        Pedido resultado = pedidoService.crearPedido(nuevoPedido);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("cliente789", resultado.getClienteId());
        assertEquals("Teclado", resultado.getProducto());
        assertEquals(50.0, resultado.getPrecio());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        assertNotNull(resultado.getFechaCreacion());

        verify(pedidoRepository, times(1)).save(nuevoPedido);
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

        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(pedido1, resultado.get());

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPedidoPorId_DebeRetornarEmptyCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(999L);

        assertFalse(resultado.isPresent());

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

        Optional<Pedido> resultado = pedidoService.actualizarEstado(1L, EstadoPedido.ENVIADO);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoPedido.ENVIADO, resultado.get().getEstado());

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido1);
    }

    @Test
    void actualizarEstado_DebeRetornarEmptyCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoService.actualizarEstado(999L, EstadoPedido.ENVIADO);

        assertFalse(resultado.isPresent());

        verify(pedidoRepository, times(1)).findById(999L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeRetornarTrueCuandoExisteYEstaPendiente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido1);

        boolean resultado = pedidoService.cancelarPedido(1L);

        assertTrue(resultado);
        assertEquals(EstadoPedido.CANCELADO, pedido1.getEstado());

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido1);
    }

    @Test
    void cancelarPedido_DebeRetornarFalseCuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        boolean resultado = pedidoService.cancelarPedido(999L);

        assertFalse(resultado);

        verify(pedidoRepository, times(1)).findById(999L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeRetornarFalseCuandoNoEstaPendiente() {
        pedido2.setEstado(EstadoPedido.ENVIADO);
        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedido2));

        boolean resultado = pedidoService.cancelarPedido(2L);

        assertFalse(resultado);
        assertEquals(EstadoPedido.ENVIADO, pedido2.getEstado());

        verify(pedidoRepository, times(1)).findById(2L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_DebeRetornarFalseCuandoYaEstaCancelado() {
        pedido1.setEstado(EstadoPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));

        boolean resultado = pedidoService.cancelarPedido(1L);

        assertFalse(resultado);
        assertEquals(EstadoPedido.CANCELADO, pedido1.getEstado());

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void eliminarPedido_DebeRetornarTrueCuandoExiste() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = pedidoService.eliminarPedido(1L);

        assertTrue(resultado);

        verify(pedidoRepository, times(1)).existsById(1L);
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarPedido_DebeRetornarFalseCuandoNoExiste() {
        when(pedidoRepository.existsById(999L)).thenReturn(false);

        boolean resultado = pedidoService.eliminarPedido(999L);

        assertFalse(resultado);

        verify(pedidoRepository, times(1)).existsById(999L);
        verify(pedidoRepository, never()).deleteById(anyLong());
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

            Optional<Pedido> resultado = pedidoService.actualizarEstado(1L, estado);

            assertTrue(resultado.isPresent());
            assertEquals(estado, resultado.get().getEstado());

            verify(pedidoRepository, times(1)).findById(1L);
            verify(pedidoRepository, times(1)).save(pedidoBase);
            
            reset(pedidoRepository);
        }
    }
}

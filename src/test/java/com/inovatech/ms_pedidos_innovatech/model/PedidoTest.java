package com.inovatech.ms_pedidos_innovatech.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PedidoTest {

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
    }

    @Test
    void gettersAndSetters_DebeFuncionarCorrectamente() {
        Long id = 1L;
        String clienteId = "cliente123";
        String producto = "Laptop";
        Double precio = 1200.0;
        EstadoPedido estado = EstadoPedido.PENDIENTE;
        LocalDateTime fechaCreacion = LocalDateTime.now();

        pedido.setId(id);
        pedido.setClienteId(clienteId);
        pedido.setProducto(producto);
        pedido.setPrecio(precio);
        pedido.setEstado(estado);
        pedido.setFechaCreacion(fechaCreacion);

        assertEquals(id, pedido.getId());
        assertEquals(clienteId, pedido.getClienteId());
        assertEquals(producto, pedido.getProducto());
        assertEquals(precio, pedido.getPrecio());
        assertEquals(estado, pedido.getEstado());
        assertEquals(fechaCreacion, pedido.getFechaCreacion());
    }

    @Test
    void onCreate_DebeAsignarValoresPorDefecto() {
        pedido.setClienteId("cliente123");
        pedido.setProducto("Mouse");
        pedido.setPrecio(25.0);

        pedido.onCreate();

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertNotNull(pedido.getFechaCreacion());
    }

    @Test
    void onCreate_NoDebeSobreescribirValoresExistente() {
        EstadoPedido estadoExistente = EstadoPedido.ENVIADO;
        LocalDateTime fechaExistente = LocalDateTime.of(2023, 1, 1, 10, 0);

        pedido.setClienteId("cliente123");
        pedido.setProducto("Mouse");
        pedido.setPrecio(25.0);
        pedido.setEstado(estadoExistente);
        pedido.setFechaCreacion(fechaExistente);

        pedido.onCreate();

        assertEquals(estadoExistente, pedido.getEstado());
        assertEquals(fechaExistente, pedido.getFechaCreacion());
    }
}

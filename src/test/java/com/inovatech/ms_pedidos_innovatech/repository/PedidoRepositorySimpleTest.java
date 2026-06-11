package com.inovatech.ms_pedidos_innovatech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "jwt.secret=test-secret-for-testing-purposes-only-min32c",
    "spring.rabbitmq.listener.simple.auto-startup=false"
})
class PedidoRepositorySimpleTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
    }

    @Test
    void crudOperations_DebeFuncionarCorrectamente() {
        Pedido pedido = new Pedido();
        pedido.setClienteId("cliente123");
        pedido.setProducto("Laptop");
        pedido.setPrecio(1200.0);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido resultado = pedidoRepository.save(pedido);

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("cliente123", resultado.getClienteId());
        assertEquals("Laptop", resultado.getProducto());
        assertEquals(1200.0, resultado.getPrecio());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());

        Optional<Pedido> encontrado = pedidoRepository.findById(resultado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(resultado.getId(), encontrado.get().getId());

        List<Pedido> todos = pedidoRepository.findAll();
        assertFalse(todos.isEmpty());
        assertTrue(todos.stream().anyMatch(p -> p.getId().equals(resultado.getId())));

        assertTrue(pedidoRepository.existsById(resultado.getId()));

        pedidoRepository.deleteById(resultado.getId());
        assertFalse(pedidoRepository.existsById(resultado.getId()));
    }

    @Test
    void findByClienteId_DebeRetornarPedidosDelCliente() {
        Pedido pedido1 = new Pedido();
        pedido1.setClienteId("cliente123");
        pedido1.setProducto("Laptop");
        pedido1.setPrecio(1200.0);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        Pedido pedido2 = new Pedido();
        pedido2.setClienteId("cliente456");
        pedido2.setProducto("Mouse");
        pedido2.setPrecio(25.0);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        Pedido pedido3 = new Pedido();
        pedido3.setClienteId("cliente123");
        pedido3.setProducto("Teclado");
        pedido3.setPrecio(50.0);
        pedido3.setEstado(EstadoPedido.PENDIENTE);
        pedido3.setFechaCreacion(LocalDateTime.now());

        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);
        pedidoRepository.save(pedido3);

        List<Pedido> pedidosCliente123 = pedidoRepository.findByClienteId("cliente123");
        assertEquals(2, pedidosCliente123.size());
        assertTrue(pedidosCliente123.stream().allMatch(p -> "cliente123".equals(p.getClienteId())));

        List<Pedido> pedidosCliente456 = pedidoRepository.findByClienteId("cliente456");
        assertEquals(1, pedidosCliente456.size());
        assertEquals("cliente456", pedidosCliente456.get(0).getClienteId());

        List<Pedido> pedidosInexistente = pedidoRepository.findByClienteId("clienteInexistente");
        assertTrue(pedidosInexistente.isEmpty());
    }

    @Test
    void findByEstado_DebeRetornarPedidosConEstado() {
        Pedido pedido1 = new Pedido();
        pedido1.setClienteId("cliente123");
        pedido1.setProducto("Laptop");
        pedido1.setPrecio(1200.0);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        Pedido pedido2 = new Pedido();
        pedido2.setClienteId("cliente456");
        pedido2.setProducto("Mouse");
        pedido2.setPrecio(25.0);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        Pedido pedido3 = new Pedido();
        pedido3.setClienteId("cliente789");
        pedido3.setProducto("Teclado");
        pedido3.setPrecio(50.0);
        pedido3.setEstado(EstadoPedido.PENDIENTE);
        pedido3.setFechaCreacion(LocalDateTime.now());

        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);
        pedidoRepository.save(pedido3);

        List<Pedido> pendientes = pedidoRepository.findByEstado(EstadoPedido.PENDIENTE);
        assertEquals(2, pendientes.size());
        assertTrue(pendientes.stream().allMatch(p -> EstadoPedido.PENDIENTE.equals(p.getEstado())));

        List<Pedido> enviados = pedidoRepository.findByEstado(EstadoPedido.ENVIADO);
        assertEquals(1, enviados.size());
        assertEquals(EstadoPedido.ENVIADO, enviados.get(0).getEstado());

        List<Pedido> cancelados = pedidoRepository.findByEstado(EstadoPedido.CANCELADO);
        assertTrue(cancelados.isEmpty());
    }

    @Test
    void findByClienteIdAndEstado_DebeRetornarPedidosDelClienteConEstado() {
        Pedido pedido1 = new Pedido();
        pedido1.setClienteId("cliente123");
        pedido1.setProducto("Laptop");
        pedido1.setPrecio(1200.0);
        pedido1.setEstado(EstadoPedido.PENDIENTE);
        pedido1.setFechaCreacion(LocalDateTime.now());

        Pedido pedido2 = new Pedido();
        pedido2.setClienteId("cliente123");
        pedido2.setProducto("Mouse");
        pedido2.setPrecio(25.0);
        pedido2.setEstado(EstadoPedido.ENVIADO);
        pedido2.setFechaCreacion(LocalDateTime.now());

        Pedido pedido3 = new Pedido();
        pedido3.setClienteId("cliente456");
        pedido3.setProducto("Teclado");
        pedido3.setPrecio(50.0);
        pedido3.setEstado(EstadoPedido.PENDIENTE);
        pedido3.setFechaCreacion(LocalDateTime.now());

        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);
        pedidoRepository.save(pedido3);

        List<Pedido> cliente123Pendientes = pedidoRepository.findByClienteIdAndEstado("cliente123", EstadoPedido.PENDIENTE);
        assertEquals(1, cliente123Pendientes.size());
        assertTrue(cliente123Pendientes.stream().allMatch(p -> 
            "cliente123".equals(p.getClienteId()) && 
            EstadoPedido.PENDIENTE.equals(p.getEstado())
        ));

        List<Pedido> cliente123Enviados = pedidoRepository.findByClienteIdAndEstado("cliente123", EstadoPedido.ENVIADO);
        assertEquals(1, cliente123Enviados.size());
        assertTrue(cliente123Enviados.stream().allMatch(p -> 
            "cliente123".equals(p.getClienteId()) && 
            EstadoPedido.ENVIADO.equals(p.getEstado())
        ));

        List<Pedido> cliente456Pendientes = pedidoRepository.findByClienteIdAndEstado("cliente456", EstadoPedido.PENDIENTE);
        assertEquals(1, cliente456Pendientes.size());
        assertTrue(cliente456Pendientes.stream().allMatch(p -> 
            "cliente456".equals(p.getClienteId()) && 
            EstadoPedido.PENDIENTE.equals(p.getEstado())
        ));

        List<Pedido> clienteInexistente = pedidoRepository.findByClienteIdAndEstado("clienteInexistente", EstadoPedido.PENDIENTE);
        assertTrue(clienteInexistente.isEmpty());
    }
}

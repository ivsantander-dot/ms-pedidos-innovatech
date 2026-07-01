package com.inovatech.ms_pedidos_innovatech.controller;

import com.inovatech.ms_pedidos_innovatech.dto.PedidoRequest;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.security.AuthenticatedUser;
import com.inovatech.ms_pedidos_innovatech.security.SecurityUtils;
import com.inovatech.ms_pedidos_innovatech.service.PedidoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/pedidos", "/api/pedidos"})
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping
    public Pedido crearPedido(@RequestBody PedidoRequest pedido, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        return pedidoService.crearPedido(pedido, currentUser);
    }

    @GetMapping
    public List<Pedido> obtenerTodosLosPedidos(Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        if (currentUser.hasRole("ADMIN")) {
            return pedidoService.obtenerTodosLosPedidos();
        }
        return pedidoService.obtenerMisPedidos(currentUser);
    }

    @GetMapping("/mis")
    public List<Pedido> obtenerMisPedidos(Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        return pedidoService.obtenerMisPedidos(currentUser);
    }

    @GetMapping(params = "usuarioId")
    public List<Pedido> obtenerPedidosPorUsuario(@RequestParam Long usuarioId, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        return pedidoService.obtenerPedidosPorCliente(String.valueOf(usuarioId), currentUser);
    }

    @GetMapping(params = "clienteId")
    public List<Pedido> obtenerPedidosPorClienteQuery(@RequestParam String clienteId, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        return pedidoService.obtenerPedidosPorCliente(clienteId, currentUser);
    }

    @GetMapping(params = "fecha")
    public List<Pedido> obtenerPedidosPorFecha(@RequestParam String fecha) {
        return pedidoService.obtenerPedidosPorFecha(fecha);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id, currentUser);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> obtenerPedidosPorCliente(@PathVariable String clienteId, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        return pedidoService.obtenerPedidosPorCliente(clienteId, currentUser);
    }

    @GetMapping("/estado/{estado}")
    public List<Pedido> obtenerPedidosPorEstado(@PathVariable EstadoPedido estado) {
        return pedidoService.obtenerPedidosPorEstado(estado);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        Optional<Pedido> pedido = pedidoService.actualizarEstado(id, estado, currentUser);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarPedido(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        if (pedidoService.cancelarPedido(id, currentUser)) {
            return ResponseEntity.ok("Pedido cancelado exitosamente");
        }
        return ResponseEntity.badRequest().body("No se pudo cancelar el pedido. Verifique que exista y este en estado PENDIENTE.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPedido(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser currentUser = securityUtils.requireUser(authentication);
        if (pedidoService.eliminarPedido(id, currentUser)) {
            return ResponseEntity.ok("Pedido eliminado exitosamente");
        }
        return ResponseEntity.notFound().build();
    }
}

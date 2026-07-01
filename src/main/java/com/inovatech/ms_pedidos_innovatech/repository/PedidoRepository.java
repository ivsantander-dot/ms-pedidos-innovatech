package com.inovatech.ms_pedidos_innovatech.repository;

import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteId(String clienteId);
    
    List<Pedido> findByEstado(EstadoPedido estado);
    
    List<Pedido> findByClienteIdAndEstado(String clienteId, EstadoPedido estado);

    List<Pedido> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}

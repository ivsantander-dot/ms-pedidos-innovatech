package com.inovatech.ms_pedidos_innovatech.dto.response;

import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        String clienteId,
        String producto,
        Double precio,
        EstadoPedido estado,
        LocalDateTime fechaCreacion
) {
}

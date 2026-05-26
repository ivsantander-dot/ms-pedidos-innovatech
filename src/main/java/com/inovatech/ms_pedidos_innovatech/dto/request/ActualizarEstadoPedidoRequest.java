package com.inovatech.ms_pedidos_innovatech.dto.request;

import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoPedidoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoPedido estado
) {
}

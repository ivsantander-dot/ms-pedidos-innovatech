package com.inovatech.ms_pedidos_innovatech.dto.request;

import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId,
        @NotBlank(message = "El producto es obligatorio")
        String producto,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        Double precio,
        EstadoPedido estado
) {
}

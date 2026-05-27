package com.inovatech.ms_pedidos_innovatech.dto.request;

import com.inovatech.ms_pedidos_innovatech.model.EstadoPedido;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoRequest(
        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId,
        @NotBlank(message = "El producto es obligatorio")
        String producto,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        Double precio,
        EstadoPedido estado,
        @Size(max = 150, message = "El nombre del destinatario no puede exceder 150 caracteres")
        String nombreDestinatario,
        @Size(max = 255, message = "La direccion de destino no puede exceder 255 caracteres")
        String direccionDestino,
        @Size(max = 120, message = "La ciudad de destino no puede exceder 120 caracteres")
        String ciudadDestino,
        @Size(max = 120, message = "La region de destino no puede exceder 120 caracteres")
        String regionDestino,
        @Size(max = 30, message = "El telefono de contacto no puede exceder 30 caracteres")
        String telefonoContacto
) {
}

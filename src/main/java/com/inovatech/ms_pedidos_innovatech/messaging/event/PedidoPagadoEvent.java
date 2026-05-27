package com.inovatech.ms_pedidos_innovatech.messaging.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PedidoPagadoEvent implements Serializable {

    private Long pedidoId;
    private Long usuarioId;
    private String nombreDestinatario;
    private String direccionDestino;
    private String ciudadDestino;
    private String regionDestino;
    private String telefonoContacto;
    private LocalDateTime fechaPago;
}

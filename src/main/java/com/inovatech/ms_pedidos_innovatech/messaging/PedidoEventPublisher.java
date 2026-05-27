package com.inovatech.ms_pedidos_innovatech.messaging;

import com.inovatech.ms_pedidos_innovatech.messaging.event.PedidoPagadoEvent;
import com.inovatech.ms_pedidos_innovatech.model.Pedido;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${pedido.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${pedido.rabbitmq.routing-key}")
    private String routingKey;

    public void publicarPedidoPagado(Pedido pedido) {
        String requestId = MDC.get("requestId");
        PedidoPagadoEvent event = PedidoPagadoEvent.builder()
                .pedidoId(pedido.getId())
                .usuarioId(Long.parseLong(pedido.getClienteId()))
                .nombreDestinatario(pedido.getNombreDestinatario())
                .direccionDestino(pedido.getDireccionDestino())
                .ciudadDestino(pedido.getCiudadDestino())
                .regionDestino(pedido.getRegionDestino())
                .telefonoContacto(pedido.getTelefonoContacto())
                .fechaPago(LocalDateTime.now())
                .build();

        CorrelationData correlationData = requestId == null || requestId.isBlank()
                ? null
                : new CorrelationData(requestId);

        MessagePostProcessor messagePostProcessor = message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            if (requestId != null && !requestId.isBlank()) {
                properties.setCorrelationId(requestId);
                properties.setHeader("X-Request-Id", requestId);
            }
            return message;
        };

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event, messagePostProcessor, correlationData);
        log.info("Evento Pedido_Pagado publicado | pedidoId: {} | exchange: {} | routingKey: {} | requestId: {}",
                pedido.getId(), exchangeName, routingKey, requestId);
    }
}

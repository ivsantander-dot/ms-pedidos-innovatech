# Eventos de dominio de ms-pedidos-innovatech

## Pedido_Pagado

Este microservicio publica `Pedido_Pagado` cuando un pedido cambia de un estado distinto de `PAGADO` a `PAGADO`.

### Flujo

`PedidoController` -> `PedidoService.actualizarEstado(...)` -> `PedidoEventPublisher` -> RabbitMQ exchange `pedido.exchange` -> queue `pedido.pagado.queue` -> `ms-logistica`

### Contrato publicado

- Exchange: `pedido.exchange`
- Routing key: `pedido.pagado`
- Queue consumidora esperada: `pedido.pagado.queue`
- Consumidor esperado: `ms-logistica`

### Payload

```json
{
  "pedidoId": 1,
  "usuarioId": 10,
  "nombreDestinatario": "Juan Perez",
  "direccionDestino": "Av. Siempre Viva 123",
  "ciudadDestino": "Santiago",
  "regionDestino": "Region Metropolitana",
  "telefonoContacto": "+56911111111",
  "fechaPago": "2026-05-26T20:15:00"
}
```

### Reglas de publicación

- Solo se publica cuando el nuevo estado es `PAGADO`.
- No se vuelve a publicar si el pedido ya estaba en `PAGADO`.
- Antes de publicar, `clienteId` debe ser numérico para mapearse a `usuarioId`.
- Antes de publicar, el pedido debe tener `nombreDestinatario`, `direccionDestino` y `ciudadDestino`.

### Validación manual

1. Levantar RabbitMQ y `ms-logistica`.
2. Crear un pedido con datos logísticos mínimos.
3. Cambiar su estado a `PAGADO`.
4. Verificar en los logs de `ms-pedidos-innovatech` la línea `Evento Pedido_Pagado publicado`.
5. Verificar en los logs de `ms-logistica` la línea `Evento recibido PedidoPagado`.

### Ejemplo con curl

Crear pedido:

```bash
curl -X POST "http://localhost:8080/api/v1/pedidos" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \
  -H "X-Request-Id: pedido-pagado-demo-001" \
  -d '{
    "clienteId": "10",
    "producto": "Laptop",
    "precio": 1200.0,
    "estado": "PENDIENTE",
    "nombreDestinatario": "Juan Perez",
    "direccionDestino": "Av. Siempre Viva 123",
    "ciudadDestino": "Santiago",
    "regionDestino": "Region Metropolitana",
    "telefonoContacto": "+56911111111"
  }'
```

Marcar como pagado:

```bash
curl -X PUT "http://localhost:8080/api/v1/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \
  -H "X-Request-Id: pedido-pagado-demo-001" \
  -d '{ "estado": "PAGADO" }'
```

# Microservicio de Pedidos - Versión Simple

## ¿Qué es esto?

Un microservicio de pedidos que permite:
- Crear pedidos
- Ver pedidos
- Cambiar el estado de los pedidos
- Cancelar pedidos

## Estados de un Pedido

Un pedido puede tener 4 estados:
1. **PENDIENTE** - El pedido está esperando ser procesado
2. **ENVIADO** - El pedido ha sido enviado al cliente
3. **RECIBIDO** - El cliente ha recibido el pedido
4. **CANCELADO** - El pedido fue cancelado

La aplicación corre en: http://localhost:8080

## Endpoints (URLs)

### Pedidos
- `POST /api/pedidos` - Crear un nuevo pedido
- `GET /api/pedidos` - Ver todos los pedidos
- `GET /api/pedidos/{id}` - Ver un pedido específico
- `DELETE /api/pedidos/{id}` - Eliminar un pedido

### Por Cliente
- `GET /api/pedidos/cliente/{clienteId}` - Ver pedidos de un cliente

### Por Estado
- `GET /api/pedidos/estado/PENDIENTE` - Ver pedidos pendientes
- `GET /api/pedidos/estado/ENVIADO` - Ver pedidos enviados
- `GET /api/pedidos/estado/RECIBIDO` - Ver pedidos recibidos
- `GET /api/pedidos/estado/CANCELADO` - Ver pedidos cancelados

### Cambiar Estado
- `PUT /api/pedidos/{id}/estado?estado=ENVIADO` - Cambiar estado de un pedido
- `PUT /api/pedidos/{id}/cancelar` - Cancelar un pedido (solo si está PENDIENTE)

## Ejemplos de uso

### Crear un pedido
```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "cliente123",
    "producto": "Laptop Gamer",
    "precio": 1500.00
  }'
```

### Ver todos los pedidos
```bash
curl http://localhost:8080/api/pedidos
```

### Ver pedidos de un cliente
```bash
curl http://localhost:8080/api/pedidos/cliente/cliente123
```

### Cambiar estado a ENVIADO
```bash
curl -X PUT http://localhost:8080/api/pedidos/1/estado?estado=ENVIADO
```

### Cancelar un pedido
```bash
curl -X PUT http://localhost:8080/api/pedidos/1/cancelar
```

## Tecnologías

- Spring Boot 4.0.6
- Java 21
- Spring Data JPA
- H2 Database
- Maven

# ms-pedidos-innovatech

## Estado de evidencia

| Categoria | Estado |
|---|---|
| Implementado | CRUD de pedidos, DTOs, errores, productor `Pedido_Pagado` |
| Configurado | MySQL/H2, RabbitMQ, perfiles, Docker |
| Validado | compilacion |
| Pendiente de validacion runtime | publicacion real a RabbitMQ y consumo en Logistica |
| No evidenciado | pruebas automatizadas end-to-end |

## 1. Descripcion general

Microservicio responsable de la gestion de pedidos. Permite crear pedidos, consultarlos, filtrarlos por cliente o estado, actualizar estado, cancelar y eliminar.

## 2. Rol dentro de la arquitectura

- API Gateway: recibe trafico oficial desde `/api/v1/pedidos/**`.
- BFF: puede ser consumido por el BFF para dashboards y tracking agregado.
- Otros microservicios: No evidenciado como consumidor HTTP saliente.
- Base de datos: H2 en desarrollo y MySQL en produccion.
- RabbitMQ: evidenciado como productor de `Pedido_Pagado`; validacion runtime pendiente.

Flujo simple:

`Cliente/Frontend -> API Gateway -> Pedidos -> Base de datos`

## 3. Stack tecnico

- Java 21
- Spring Boot 3.5.14
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- H2
- MySQL
- Actuator
- Swagger/OpenAPI
- Docker

## 4. Puerto del servicio

| Concepto | Valor |
|---|---|
| Puerto esperado | `8082` |
| Puerto configurado | `${SERVER_PORT:8082}` |
| Archivo donde se define | `src/main/resources/application.properties` |
| Variable de entorno asociada | `SERVER_PORT` |

## 5. Variables de entorno

| Variable | Descripcion | Valor por defecto | Obligatoria | Riesgo/observacion |
|---|---|---|---|---|
| `SERVER_PORT` | Puerto HTTP del servicio | `8082` | No | Debe alinearse con Gateway/Docker |
| `JWT_SECRET` | Secreto para validar JWT | No evidenciado en default | Si | Critica |
| `APP_SECURITY_DOCS_PUBLIC` | Control de docs publicas | `false` en base, `true` en dev | No | No abrir en prod |
| `PEDIDOS_MYSQL_HOST` | Host MySQL prod | No default en prod | Si en prod | Debe existir |
| `PEDIDOS_MYSQL_PORT` | Puerto MySQL prod | No default en prod | Si en prod | Debe existir |
| `PEDIDOS_MYSQL_DATABASE` | Nombre DB prod | No default en prod | Si en prod | Debe existir |
| `PEDIDOS_MYSQL_USERNAME` | Usuario DB prod | No default en prod | Si en prod | No usar root |
| `PEDIDOS_MYSQL_PASSWORD` | Password DB prod | No default en prod | Si en prod | Sensible |

## 6. Base de datos

| Elemento | Valor |
|---|---|
| Motor | H2 en dev, MySQL en prod |
| Base de datos | `pedidosdb` en H2 dev, `${PEDIDOS_MYSQL_DATABASE}` en prod |
| Entidades | `Pedido` |
| Repositories | `PedidoRepository` |
| ddl-auto | `update` en dev, `validate` en prod |
| show-sql | `true` en dev, `false` en prod |

Riesgos o pendientes:

- El `Dockerfile` expone `8080`, pero la configuracion del servicio usa `8082`.
- El productor `Pedido_Pagado` esta implementado; su validacion runtime con Logistica sigue pendiente.

## 7. Endpoints principales

| Metodo | Endpoint | Descripcion | Auth requerida | Request | Response |
|---|---|---|---|---|---|
| `POST` | `/api/v1/pedidos` | Crea un pedido | Si | `PedidoRequest` | `PedidoResponse` |
| `GET` | `/api/v1/pedidos` | Lista todos los pedidos | Si | No aplica | `List<PedidoResponse>` |
| `GET` | `/api/v1/pedidos/{id}` | Busca pedido por id | Si | No aplica | `PedidoResponse` |
| `GET` | `/api/v1/pedidos/cliente/{clienteId}` | Filtra pedidos por cliente | Si | No aplica | `List<PedidoResponse>` |
| `GET` | `/api/v1/pedidos/estado/{estado}` | Filtra pedidos por estado | Si | No aplica | `List<PedidoResponse>` |
| `PUT` | `/api/v1/pedidos/{id}/estado` | Actualiza estado del pedido | Si | `ActualizarEstadoPedidoRequest` | `PedidoResponse` |
| `PUT` | `/api/v1/pedidos/{id}/cancelar` | Cancela pedido | Si | No aplica | `PedidoResponse` |
| `DELETE` | `/api/v1/pedidos/{id}` | Elimina pedido | Si | No aplica | `204 No Content` |
| `GET` | `/api/v1/pedidos/health` | Health funcional propio | No evidenciado con seguridad | No aplica | `String` o similar; pendiente de verificacion |

## 8. Seguridad

- Usa Spring Security: Si.
- Valida JWT: Si.
- Depende del Gateway: No estrictamente; puede validar acceso directo.
- Endpoints publicos: No evidenciado en controller; revisar `SecurityConfig`. Pendiente de verificacion exacta.
- Endpoints protegidos: Controlador principal de pedidos.
- Riesgos detectados:
  - El endpoint `/api/v1/pedidos/health` existe en `HealthController`; su politica exacta debe revisarse con `SecurityConfig`.
  - El puerto expuesto por Dockerfile no coincide con el configurado por propiedades.

## 9. Integraciones

| Origen | Destino | Tipo | URL/variable | Estado |
|---|---|---|---|---|
| Gateway | Pedidos | HTTP | `/api/v1/pedidos/**` | Evidenciado |
| BFF | Pedidos | HTTP | No evidenciado aqui; esperado por arquitectura | Pendiente de verificacion |
| Pedidos | Base de datos | JPA/JDBC | H2 dev / `PEDIDOS_MYSQL_*` prod | Evidenciado |

## 10. Eventos RabbitMQ

Se evidencia el evento `Pedido_Pagado` como productor hacia `pedido.exchange` con routing key `pedido.pagado`. La validacion runtime sigue pendiente.

## 11. Ejecucion local

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Pruebas basicas:

```bash
curl http://localhost:8082/api/v1/pedidos -H "Authorization: Bearer <JWT>"
```

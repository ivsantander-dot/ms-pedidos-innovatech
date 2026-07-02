# ms-pedidos-innovatech

## Estado de evidencia

| Categoria | Estado |
| --- | --- |
| Implementado | CRUD de pedidos, estados, consulta por usuario, JWT, Actuator |
| Configurado | MySQL, perfiles `local/aws`, Docker |
| Validado | compilacion |
| Pendiente runtime | flujo real completo via Gateway, BFF y RDS/AWS |
| No evidenciado | RabbitMQ operativo extremo a extremo |

## 1. Descripcion general

Microservicio encargado de registrar, consultar y actualizar pedidos. Expone operaciones REST para creacion, historial, estados y administracion funcional del dominio de pedidos.

## 2. Rol dentro de la arquitectura

- API Gateway: entrada oficial para `/api/v1/pedidos/**` y compatibilidad legacy con `/api/pedidos/**`.
- BFF: puede consumir informacion de pedidos para dashboard y tracking consolidado.
- Persistencia: MySQL propia.
- RabbitMQ: el contenedor local recibe variables RabbitMQ, pero la evidencia runtime del flujo completo sigue pendiente.

Flujo base:

`Frontend -> API Gateway -> Pedidos -> MySQL`

## 3. Stack tecnico

- Java 21
- Spring Boot
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Spring Boot Actuator
- Docker

## 4. Puerto y exposicion

| Item | Valor |
| --- | --- |
| Puerto interno | `8082` |
| Configuracion | `${SERVER_PORT:8082}` |
| Exposicion publica oficial | via API Gateway |
| Exposicion directa recomendada | no |

## 5. Perfiles soportados

| Perfil | Uso | Estado |
| --- | --- | --- |
| `local` | Docker local / desarrollo | Configurado |
| `aws` | ECS Fargate + RDS | Configurado |

Notas:

- El perfil por defecto es `local`.
- `ddl-auto` se mantiene en `update`.
- No se afirma runtime AWS como validado.

## 6. Variables de entorno requeridas

### Comunes

| Variable | Uso |
| --- | --- |
| `SERVER_PORT` | puerto HTTP |
| `JWT_SECRET` | secreto JWT |

### Base de datos

| Variable | Local | AWS |
| --- | --- | --- |
| `DB_HOST` | opcional, default `localhost` | requerida |
| `DB_PORT` | opcional, default `3306` | requerida |
| `DB_NAME` | opcional, default `innovatech_pedidos` | requerida |
| `DB_USERNAME` | opcional | requerida |
| `DB_PASSWORD` | opcional | requerida |

Compatibilidad adicional:

- `PEDIDOS_MYSQL_HOST`
- `PEDIDOS_MYSQL_PORT`
- `PEDIDOS_MYSQL_DATABASE`
- `PEDIDOS_MYSQL_USERNAME`
- `PEDIDOS_MYSQL_PASSWORD`

### RabbitMQ

| Variable | Estado |
| --- | --- |
| `RABBITMQ_HOST` | configurada en Docker local |
| `RABBITMQ_PORT` | configurada en Docker local |
| `RABBITMQ_USERNAME` | configurada en Docker local |
| `RABBITMQ_PASSWORD` | configurada en Docker local |

## 7. Endpoints principales

| Metodo | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/pedidos` | crear pedido |
| `GET` | `/api/v1/pedidos` | listar pedidos |
| `GET` | `/api/v1/pedidos/{id}` | obtener pedido |
| `GET` | `/api/v1/pedidos/cliente/{clienteId}` | historial por cliente |
| `PUT` | `/api/v1/pedidos/{id}/estado` | actualizar estado |
| `PUT` | `/api/v1/pedidos/{id}/cancelar` | cancelar pedido |
| `GET` | `/actuator/health` | healthcheck |

## 8. Integracion y dependencias

| Componente | Tipo | Estado |
| --- | --- | --- |
| API Gateway | HTTP entrante | Evidenciado |
| BFF | consumo interno potencial | Evidenciado por configuracion |
| MySQL | persistencia | Evidenciado |
| RabbitMQ | integracion configurada | Pendiente runtime completo |

## 9. Docker y build

- `Dockerfile` presente y validado.
- Imagen preparada para `SPRING_PROFILES_ACTIVE=aws` por defecto en contenedor.
- En `docker-compose.yml` local el servicio corre con perfil `local`.

Comandos utiles:

```bash
./mvnw.cmd -q -DskipTests compile
docker build -t innovatech-pedidos .
```

## 10. Estado actual de validacion

- `Validado`: compilacion.
- `Configurado`: perfiles `local/aws`, variables `DB_*`, Docker.
- `Pendiente runtime`: integracion completa con BFF, RabbitMQ y despliegue AWS.

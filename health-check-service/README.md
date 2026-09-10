# Health Check Service

Primer servicio REST empresarial construido con **Spring Boot**. Sirve como punto
de partida para el resto de la aplicación: incluye configuración base,
persistencia con JPA, un endpoint de salud (*health check*) y un recurso REST
de ejemplo (`Producto`) documentado automáticamente con OpenAPI/Swagger.

## Requisitos previos

- JDK 17 o superior
- Maven 3.9+ (o usar el wrapper `./mvnw` si lo agregas con `mvn -N wrapper:wrapper`)
- IDE recomendado: IntelliJ IDEA / Spring Tool Suite / VS Code con extensión Java

## Cómo ejecutar el proyecto

```bash
# 1. Descargar dependencias y compilar
mvn clean install

# 2. Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación arrancará en `http://localhost:8080`.

## Base de datos

Por defecto el proyecto usa **H2 en memoria**, para que cualquiera pueda
ejecutarlo sin instalar un motor de base de datos externo. Cada vez que la
app arranca se crea una base limpia (`spring.jpa.hibernate.ddl-auto: update`).

- Consola web de H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:empresadb`
  - Usuario: `sa`
  - Contraseña: *(vacía)*

Con esto puedes tomar la **evidencia de conexión a la base de datos**: entra a
la consola, ejecuta `SELECT * FROM PRODUCTOS;` y verás la tabla creada por JPA.

### Cómo cambiar a MySQL o PostgreSQL

Si tu entorno lo requiere, reemplaza en `pom.xml` la dependencia de H2 por el
driver correspondiente:

```xml
<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

Y actualiza `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/empresadb
    username: root
    password: tu_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## Endpoints disponibles

### Health Check (Parte E)

```
GET /api/v1/health
```

Respuesta esperada:

```json
{
  "status": "UP",
  "application": "health-check-service",
  "version": "1.0.0",
  "database": "CONNECTED",
  "timestamp": "2026-08-13T10:15:30"
}
```

### Recurso Producto (Parte F)

| Método | Endpoint                | Descripción                    |
|--------|--------------------------|---------------------------------|
| POST   | `/api/v1/productos`      | Crea un producto                |
| GET    | `/api/v1/productos`      | Lista todos los productos       |
| GET    | `/api/v1/productos/{id}` | Busca un producto por id        |

Ejemplo de creación (`POST /api/v1/productos`):

```json
{
  "nombre": "Laptop Empresarial",
  "descripcion": "Laptop de 15 pulgadas para uso corporativo",
  "precio": 2500.50
}
```

Prueba rápida con `curl`:

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Crear producto
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laptop Empresarial","descripcion":"15 pulgadas","precio":2500.50}'

# Listar productos
curl http://localhost:8080/api/v1/productos
```

## Documentación OpenAPI (Parte G)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- JSON OpenAPI: `http://localhost:8080/api-docs`

Ahí verás documentados automáticamente ambos controladores
(`HealthController` y `ProductoController`), con los DTOs de entrada/salida,
validaciones y ejemplos.

## Estructura del proyecto (Parte B)

```
src/main/java/com/empresa/healthcheck
├── HealthCheckServiceApplication.java   # Clase principal
├── config/          # Configuración (OpenAPI, etc.)
├── controller/       # Controladores REST (capa de presentación)
├── dto/               # Objetos de transferencia de datos
├── entity/            # Entidades JPA (capa de persistencia)
├── repository/         # Repositorios Spring Data JPA
├── service/            # Interfaces de lógica de negocio
│   └── impl/           # Implementaciones de los servicios
└── exception/          # Manejo centralizado de errores
```

## Evidencias sugeridas para la entrega

1. **Compilación correcta:** captura de `mvn clean install` terminando en `BUILD SUCCESS`.
2. **Conexión a base de datos:** captura de la consola H2 mostrando la tabla `PRODUCTOS`.
3. **Health check:** captura de `GET /api/v1/health` respondiendo `"status": "UP"`.
4. **Recurso REST:** capturas de `POST` y `GET` sobre `/api/v1/productos` (Postman/Insomnia/curl).
5. **OpenAPI:** captura de Swagger UI (`/swagger-ui.html`) mostrando ambos endpoints documentados.

Ver también `ARQUITECTURA.md` para la descripción breve de la arquitectura utilizada.

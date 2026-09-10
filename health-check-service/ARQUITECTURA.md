# Documento de Arquitectura — Health Check Service

## 1. Objetivo

Este proyecto implementa la base de un backend empresarial usando Spring
Boot, siguiendo una **arquitectura en capas** (layered architecture), que
separa responsabilidades y facilita el mantenimiento y la escalabilidad del
sistema conforme se agreguen nuevos módulos.

## 2. Stack tecnológico

| Componente          | Tecnología                          |
|---------------------|--------------------------------------|
| Lenguaje             | Java 17                              |
| Framework            | Spring Boot 3.3.4                    |
| Persistencia         | Spring Data JPA / Hibernate          |
| Base de datos        | H2 (en memoria, configurable a MySQL/PostgreSQL) |
| Validación           | Jakarta Bean Validation              |
| Documentación de API | Springdoc OpenAPI (Swagger UI)       |
| Reducción de boilerplate | Lombok                          |
| Build                | Maven                                |

## 3. Arquitectura en capas

```
┌──────────────────────────────┐
│        Controller             │  ← Recibe peticiones HTTP, valida entrada,
│  (HealthController,           │    delega al Service. Expone DTOs, nunca
│   ProductoController)         │    entidades.
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│           Service              │  ← Contiene la lógica de negocio.
│  (ProductoService + Impl)      │    Convierte Entidad <-> DTO.
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│         Repository             │  ← Acceso a datos mediante Spring Data
│    (ProductoRepository)        │    JPA. Sin SQL manual para operaciones
│                                 │    CRUD básicas.
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│           Entity                │  ← Representa la tabla en la base de
│         (Producto)              │    datos (mapeo JPA).
└────────────────────────────────┘
```

Capas transversales:

- **DTO**: objetos que viajan entre el cliente y el controlador
  (`ProductoRequestDTO`, `ProductoResponseDTO`, `HealthResponseDTO`).
  Evitan exponer directamente la entidad JPA y permiten validar la entrada
  independientemente del modelo de datos.
- **Exception**: manejo centralizado de errores con `@RestControllerAdvice`,
  que traduce excepciones de negocio en respuestas HTTP consistentes.
- **Config**: configuración de infraestructura (en este caso, metadatos de
  OpenAPI).

## 4. Flujo de una petición (ejemplo: crear un producto)

1. El cliente envía `POST /api/v1/productos` con un JSON.
2. `ProductoController` recibe la petición, Spring convierte el JSON a
   `ProductoRequestDTO` y ejecuta las validaciones (`@Valid`).
3. El controlador delega en `ProductoService`.
4. `ProductoServiceImpl` transforma el DTO a la entidad `Producto` y la
   persiste mediante `ProductoRepository`.
5. El resultado se transforma de vuelta a `ProductoResponseDTO` y se
   retorna al cliente con código `201 Created`.
6. Si algo falla (ej. producto no encontrado, validación incorrecta),
   `GlobalExceptionHandler` intercepta la excepción y responde con un
   error estructurado y el código HTTP correspondiente.

## 5. Decisiones de diseño

- **Uso de DTOs**: se decidió no exponer las entidades JPA directamente en la
  API para evitar acoplar el contrato público del servicio al modelo interno
  de datos, y para poder aplicar validaciones específicas a la entrada.
- **H2 en memoria**: se eligió para que el proyecto sea ejecutable de
  inmediato por cualquier evaluador sin depender de instalar un motor de
  base de datos externo. La configuración es fácilmente reemplazable por
  MySQL/PostgreSQL cambiando solo el driver y el `application.yml`.
- **Interfaz + implementación en la capa Service**: permite desacoplar el
  contrato de negocio de su implementación, facilitando pruebas unitarias
  con mocks y futuras extensiones (por ejemplo, distintas estrategias de
  negocio).
- **Documentación automática con Springdoc**: se generan `swagger-ui.html`
  y `api-docs` sin esfuerzo adicional, simplemente anotando los DTOs y
  controladores, lo que mantiene la documentación siempre sincronizada con
  el código.

## 6. Endpoints principales

| Método | Endpoint                  | Descripción                       |
|--------|----------------------------|-------------------------------------|
| GET    | `/api/v1/health`           | Estado de salud de la aplicación y la BD |
| POST   | `/api/v1/productos`        | Crea un producto                    |
| GET    | `/api/v1/productos`        | Lista todos los productos           |
| GET    | `/api/v1/productos/{id}`   | Obtiene un producto por id          |
| GET    | `/swagger-ui.html`         | Documentación interactiva (OpenAPI) |
| GET    | `/h2-console`               | Consola de la base de datos H2      |

## 7. Próximos pasos sugeridos

- Agregar operaciones `PUT`/`DELETE` sobre `Producto`.
- Incorporar pruebas unitarias e integración por capa (Repository, Service, Controller).
- Externalizar configuración sensible (usuario/contraseña de BD) mediante
  variables de entorno o un gestor de secretos.
- Migrar a un motor de base de datos productivo (MySQL/PostgreSQL) en
  ambientes de QA/producción.

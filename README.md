# ProductsApplication

API REST para la gestión de productos y su historial de precios, desarrollada como prueba técnica de backend utilizando Kotlin, Spring Boot, MySQL y Liquibase.

La aplicación permite crear productos, registrar precios, consultar el precio vigente de un producto en una fecha determinada y obtener su historial completo de precios.

## Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **Framework:** Spring Boot
- **Java:** 21
- **Base de datos:** MySQL 8
- **Persistencia:** Spring Data JPA / Hibernate
- **Migraciones de base de datos:** Liquibase (XML)
- **Herramienta de construcción:** Gradle
- **Testing:** JUnit 5, Mockito, MockMvc y Testcontainers
- **Infraestructura:** Docker Compose

## Arquitectura

La aplicación sigue una estructura por capas inspirada en la **Arquitectura Hexagonal**, manteniendo una separación clara entre la lógica de negocio y la infraestructura.

Las principales responsabilidades se distribuyen de la siguiente manera:

- **Dominio:** Modelos de negocio y validación de sus invariantes.
- **Aplicación:** Casos de uso que implementan las operaciones de negocio.
- **Puertos:** Interfaces que definen las operaciones de persistencia.
- **Adaptadores:** Implementaciones de los puertos mediante JPA.
- **Controladores:** Exposición de endpoints HTTP y gestión de peticiones.
- **Mappers:** Conversión entre modelos de dominio, entidades de persistencia y DTOs de la API.

Las operaciones de lectura y escritura se organizan por separado mediante los paquetes `command` y `query`, siguiendo una aproximación ligera a **CQRS**.

Las entidades de Spring Data JPA permanecen en la capa de persistencia y no se exponen directamente a través de la API REST.

## Reglas de negocio

### Validación de precios

Cada precio debe cumplir las siguientes condiciones:

- El importe debe ser mayor que cero.
- La fecha de inicio es obligatoria.
- La fecha de finalización es opcional.
- Cuando se informa, la fecha de finalización debe ser posterior a la fecha de inicio.
- Un producto no puede tener períodos de precios solapados.

### Límites de fechas

Los intervalos de vigencia se consideran **inclusivos**.

Un precio es aplicable a una fecha determinada cuando:

```text
initDate <= requestedDate
AND
(endDate IS NULL OR endDate >= requestedDate)
```

Una fecha de finalización `null` representa un período de vigencia sin fecha de caducidad definida.

### Concurrencia

La creación de precios utiliza una transacción con nivel de aislamiento `READ_COMMITTED` y un bloqueo pesimista sobre el producto asociado.

Este mecanismo evita que dos peticiones concurrentes creen períodos de precios solapados, siempre que todas las operaciones de escritura sigan el mismo protocolo de bloqueo.

## Endpoints de la API

Todos los endpoints implementados utilizan el prefijo `/api/v1`.

### 1. Crear un producto

`POST /api/v1/products`

**Request:**

```json
{
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada"
}
```

**Respuesta:** `201 Created`.

### 2. Crear un precio para un producto

`POST /api/v1/products/{id}/prices`

**Request:**

```json
{
  "value": 99.99,
  "initDate": "2024-01-01",
  "endDate": "2024-06-30"
}
```

**Respuesta:** `201 Created`.

El campo `endDate` puede contener `null` cuando el precio no tiene una fecha de finalización definida.

### 3. Obtener el precio vigente en una fecha

`GET /api/v1/products/{id}/prices?date=2024-04-15`

**Respuesta:** `200 OK`

```json
{
  "value": 99.99
}
```

Posibles resultados:

- `200 OK`: Existe un precio vigente para la fecha indicada.
- `204 No Content`: El producto existe, pero no tiene un precio vigente para esa fecha.
- `404 Not Found`: El producto no existe.

### 4. Obtener el historial completo de precios

`GET /api/v1/products/{id}/prices`

**Respuesta:** `200 OK`

```json
{
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada",
  "prices": [
    {
      "value": 99.99,
      "initDate": "2024-01-01",
      "endDate": "2024-06-30"
    },
    {
      "value": 199.99,
      "initDate": "2025-01-01",
      "endDate": "2025-06-30"
    }
  ]
}
```

Los precios se devuelven ordenados cronológicamente por `initDate`, de forma ascendente.

Si el producto existe, pero todavía no tiene precios registrados, se devuelve una lista `prices` vacía.

## Decisiones de diseño de la API

### Endpoints de consulta de precios

Las siguientes peticiones comparten intencionadamente la misma ruta:

```http
GET /api/v1/products/{id}/prices
GET /api/v1/products/{id}/prices?date=2024-04-15
```

El parámetro de consulta `date` determina si se recupera un único precio vigente o el historial completo de precios.

Spring MVC diferencia ambas operaciones mediante condiciones sobre los parámetros de la petición.

Este enfoque mantiene la estructura de endpoints especificada en el enunciado de la prueba técnica, utilizando la fecha como criterio de consulta.

### Semántica de las respuestas HTTP

La ausencia de un precio vigente se considera una situación válida de negocio, no necesariamente un error.

Por este motivo, la API devuelve `204 No Content` cuando el producto existe, pero no dispone de un precio aplicable a la fecha solicitada.

En cambio, cuando el producto solicitado no existe, se devuelve `404 Not Found`.

### Precisión de los precios

Los precios se almacenan en la base de datos con una precisión de seis decimales.

Los valores se redondean a dos decimales mediante `HALF_UP` al generar las respuestas de la API.

Esto permite conservar la precisión original en persistencia y ofrecer una representación consistente a los consumidores.

## Gestión de errores

Se utiliza un manejador global de excepciones que transforma los errores de dominio y validación en respuestas HTTP.

Ejemplo:

```json
{
  "code": "PRICE_DATE_OVERLAP",
  "message": "Price date range overlaps with an existing price for product with id: ..."
}
```

Los principales escenarios contemplados son:

| Situación | Estado HTTP |
|---|---|
| Producto inexistente | `404 Not Found` |
| Períodos de precios solapados | `409 Conflict` |
| Cuerpo de petición inválido | `400 Bad Request` |
| Error de validación de campos | `400 Bad Request` |

## Ejecución de la aplicación

### Requisitos previos

- JDK 21
- Docker con soporte para Docker Compose

### 1. Iniciar MySQL

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Docker Compose inicia la base de datos MySQL utilizada por la aplicación.

### 2. Iniciar la aplicación

```bash
./gradlew bootRun
```

Liquibase ejecuta automáticamente las migraciones de base de datos durante el arranque.

La API estará disponible en:

```text
http://localhost:8080
```

La configuración de conexión a la base de datos se encuentra definida en la configuración de Spring del proyecto.

## Ejecución de los tests

Para ejecutar todas las pruebas:

```bash
./gradlew clean test
```

La batería de pruebas incluye:

- Tests de validación del dominio.
- Tests unitarios de los casos de uso mediante Mockito.
- Tests de controladores HTTP con MockMvc.
- Tests de integración de persistencia con MySQL y Testcontainers.
- Test de concurrencia durante la creación de precios.

**Docker debe estar disponible** para ejecutar las pruebas que utilizan Testcontainers.

## Base de datos

El esquema de la base de datos se gestiona mediante migraciones de Liquibase.

Las principales tablas son:

- `products`: Información de los productos.
- `prices`: Importes, períodos de vigencia y relación con el producto correspondiente.

La relación es de uno a muchos: un producto puede tener varios precios y cada precio pertenece a un único producto.

Las claves foráneas, las restricciones de integridad y los índices complementan las validaciones implementadas en la lógica de negocio.

## Consideraciones finales

El proyecto prioriza una separación clara de responsabilidades, la facilidad para realizar pruebas y la definición explícita de las reglas de negocio, evitando introducir abstracciones innecesarias.

La autenticación y la autorización quedan fuera del alcance de esta prueba técnica.
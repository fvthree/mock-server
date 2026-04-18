# Mock Server

Spring Boot API mock server with Docker support.

## Run locally (without Docker)

Requirements:
- Java 21
- Maven 3.9+

```bash
mvn spring-boot:run
```

Application URL:

- http://localhost:10000/

Default port comes from `server.port` in `application.properties` (override with `PORT` or `-Dserver.port=...`).

## Dynamic mock API behavior

All endpoints under `/api/**` are resolved to files under `src/main/resources/mocks`.

Mapping examples:
- `GET /api/users/42` -> `mocks/users/42.json`
- optional metadata -> `mocks/users/42.meta.json`
- `GET /api` or `GET /api/` -> `mocks/index.json`

Metadata file controls custom HTTP response behavior:
- `status` (e.g. 200, 202, 404, 500)
- `headers` (any custom HTTP headers)
- `delayMs` (artificial response delay)
- `contentType` (defaults to `application/json`)

Example files included:
- `src/main/resources/mocks/users/42.json`
- `src/main/resources/mocks/users/42.meta.json`

## Run with Docker

Build and start container:

```bash
docker compose up --build
docker compose up
```

The container serves the app on port **10000**; Compose maps **host 8080 → container 10000**, so you use the same URL as typical Spring examples:

- http://localhost:8080/

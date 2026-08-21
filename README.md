# Adventure Book API

A REST API for browsing a collection of gamebooks.

## Domain model

- **Book** — title, author, difficulty (`EASY`/`MEDIUM`/`HARD`), and categories (free
  text, e.g. `FICTION`, `HORROR`, `ADVENTURE`).

## API

Swagger UI: http://localhost:8080/swagger-ui.html

| Endpoint | Description |
|---|---|
| `GET /api/books?title=&author=&category=&difficulty=&page=&size=` | search/list books |

## Running locally

```bash
docker-compose up --build
```

This starts Postgres and the API on `http://localhost:8080`. Flyway migrates the
schema on startup.

## Tests

```bash
./mvnw test
```

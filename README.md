# Spring Boot Redis Caching Demo

A **Java 17 / Spring Boot 3.5** learning example combining Spring Data JPA (MySQL) with Redis-backed caching. The project is a demonstration, not a production-ready service.

## Quick start with Docker Compose

Requires Docker with Compose v2. No local Java, Maven, MySQL, or Redis installation is needed for this route.

```bash
docker compose up --build -d
docker compose ps
```

The app is available on **http://localhost:8080** (container port 9191). MySQL and Redis are accessible only on the Compose network; they are not published to the host. The MySQL data is stored in the `mysql_data` named volume.

```bash
docker compose logs -f app
docker compose down
# To delete the local database volume too: docker compose down -v
```

The Compose configuration builds the current source code using **Java 17**, rather than pulling the older prebuilt Java 8 image. Database and Redis health checks gate application startup. The tracked `.env` contains public demo-only passwords and is loaded by Docker Compose. Never put real credentials in this file.

## Run locally without Docker

Requires JDK 17, Maven, a running MySQL server with a `javatechie` database, and Redis.

| Variable | Purpose | Default |
|---|---|---|
| `DB_PASSWORD` | **Required** MySQL password | none |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_URL` | JDBC URL | `jdbc:mysql://localhost:3306/javatechie` |
| `REDIS_HOST` | Redis host | `redis` |

Set `REDIS_HOST=localhost` when Redis runs directly on your machine, and provide `DB_PASSWORD` through the environment.

```bash
mvn -B -Dtest=ProductServiceTest test
mvn spring-boot:run
```

The isolated Mockito/JUnit unit tests do not need MySQL or Redis. The existing `@SpringBootTest` context test does require configured infrastructure; running the **full** `mvn test` suite without it may fail. The GitHub Actions unit-test job deliberately selects `ProductServiceTest` only.

## Cache consistency

The `products` cache stores individual products by their persisted database ID and the full `GET /products` result under Spring's default no-argument cache key. Creating, updating, or deleting a product invalidates the cached list; updates refresh the individual product entry, and deletions evict it. The save path uses the **persisted ID** (not a new entity's initial ID).

The isolated unit-test job now also exercises these cache annotations through a real Spring cache proxy with an in-memory `ConcurrentMapCacheManager`. It does **not** prove Redis serialization, network connectivity, or cross-instance invalidation.

## CI and engineering notes

GitHub Actions runs isolated service tests on Java 17 and checks that the container image builds. Docker Compose configuration is validated with the sample environment file; this is **not** a live MySQL/Redis integration test.

- `spring.jpa.hibernate.ddl-auto=update` is for demo convenience, not production schema management.
- Cache consistency, connection recovery, secret management, and real broker/database integration tests still need production-oriented treatment.
- The tracked `.env` contains demo credentials only; never commit real secrets.

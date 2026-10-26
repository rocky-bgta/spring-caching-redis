# Spring Boot Redis Caching Demo

A Java 8 / Spring Boot 2.7 sample combining MySQL-backed Spring Data JPA with Redis. This repository is a learning demo, not a production-ready template.

## Prerequisites

- JDK 8 and Maven
- MySQL with a database named `javatechie`
- Redis (the application defaults to hostname `redis`, which is suitable for a matching Docker Compose service)

## Configuration

The application reads its database password from the environment; no password is committed to source control.

| Variable | Purpose | Default |
|---|---|---|
| `DB_PASSWORD` | **Required** MySQL password | none |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/javatechie` |
| `REDIS_HOST` | Redis hostname | `redis` |

For a locally installed Redis server, set `REDIS_HOST=localhost`. Set environment variables in your shell or IDE run configuration. Avoid committing real credentials.

## Run

```bash
mvn test
mvn spring-boot:run
```

The application listens on port **9191** by default. Check the source controllers for the available endpoints.

## Docker

The existing Dockerfile can be used to build an image:

```bash
docker build -t spring-boot-caching-redis:local .
```

When running a container, provide `DB_PASSWORD`, `DB_URL`, and `REDIS_HOST` for services reachable from that container; `localhost` inside a container does **not** refer to the host machine. Example port mapping: `-p 8080:9191`.

## Engineering notes

- This example currently uses Spring Boot 2.7 and Java 8. Upgrading to a supported Spring Boot and Java baseline is a separate change that should include compatibility testing.
- Database schema updates are enabled through `spring.jpa.hibernate.ddl-auto=update`; avoid this setting in production.
- The example does not claim production readiness or verified CI coverage.

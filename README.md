# Dropwizard PostgreSQL REST API

How to start the application
---

This RESTful API is an example that exposes services through a REST interface, manipulating information in a PostgreSQL database.

## Technologies
- Gradle
- Kotlin
- Dropwizard
- PostgreSQL
- jdbi
- Flyway
- Swagger
- Docker

## Development

1. Run `./gradlew clean shadowJar` to build the application.
2. Run `docker-compose up` to start the containers.
3. Go to `http://localhost:8080`.

Swagger
---
To check that your application is running, enter URL `http://localhost:8080/swagger`.

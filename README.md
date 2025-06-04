# Field Reservation System

A sports field reservation system built with Kotlin and Spring Boot.

## Description

This application provides a platform for managing sports field reservations. It allows users to view available sports fields and make reservations for specific time slots. Managers can manage their fields and reservations.

## Technologies Used

- **Kotlin 2.0.21** - Modern JVM language with coroutines support
- **Spring Boot 3.4.6** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Security** - Authentication and authorization
- **Spring Data R2DBC** - Reactive database access
- **Spring Actuator** - Application monitoring and management
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **GraphQL (Netflix DGS)** - API layer
- **Kotest** - Testing framework
- **TestContainers** - Integration testing with real database instances
- **ktlint** - Kotlin linting and code style enforcement

## Prerequisites

- JDK 21
- Docker (for running PostgreSQL and integration tests)
- Gradle (wrapper included)

## Local setup

1. Clone the repository
2. Start a PostgreSQL instance using Docker:
   ```
   docker run --name field_reservation_db -e POSTGRES_DB=field_reservation -e POSTGRES_USER=field_reservation_db_user -e POSTGRES_PASSWORD=field_reservation_db_password -p 54328:5432 -d postgres:16.2
   ```
3. Build the application:
   ```
   ./gradlew build
   ```

4. Run the application with local profile:
   ```
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

Once the application is running:

- The application runs on port 8081
- GraphQL API endpoint: http://localhost:8081/public/graphql
- GraphiQL (GraphQL IDE): http://localhost:8081/graphiql
- Management endpoints: http://localhost:8081/management/

## Development

### Running Tests

```
./gradlew test
```

### Code Style

This project uses ktlint for code style enforcement. Run the following to check and fix code style issues:

```
./gradlew ktlintCheck    # Check for code style issues
./gradlew ktlintFormat   # Fix code style issues automatically
```

### Database Migrations

Database migrations are managed with Flyway and can be found in `src/main/resources/db/migration`.

### GraphQL API

The GraphQL schema is defined in `src/main/resources/schema/schema.graphql`.

## Deployment

TODO: Add deployment instructions

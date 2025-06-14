# Sports Field Reservation System

A sports field reservation system built with Kotlin and Spring Boot.

## Description

This application provides a platform for managing sports field reservations. It allows users to view available sports fields and make reservations for specific time slots. Managers can manage their fields and reservations.

## Technical Overview
The application is written in reactive style using Kotlin coroutines and R2DBC for database access. It uses GraphQL for the API layer.  
Domain driven design principles are applied to ensure a good maintainability.  
Testing is done using Kotest for unit tests and Dgs tooling + TestContainers for integration tests with a real PostgreSQL database.

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

### Local setup using gradle

1. Start a PostgreSQL instance using Docker:
   ```
   docker run --name field_reservation_db -e POSTGRES_DB=field_reservation -e POSTGRES_USER=field_reservation_db_user -e POSTGRES_PASSWORD=field_reservation_db_password -p 54328:5432 -d postgres:16.2
   ```
2. Build the application:
   ```
   ./gradlew build
   ```

3. Run the application with local profile:
   ```
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

### Local setup using IntelliJ IDEA

1. Start a PostgreSQL instance using Docker:
   ```
   docker run --name field_reservation_db -e POSTGRES_DB=field_reservation -e POSTGRES_USER=field_reservation_db_user -e POSTGRES_PASSWORD=field_reservation_db_password -p 54328:5432 -d postgres:16.2
   ```
2.  Run the application with local profile:
    - Create a new `Spring Boot` configuration.
    - Set the main class to `cz.jurca.fieldreservationsystem.FieldReservationSystemApplicationKt`.
    - Set active profiles to `local`.
    - run it

### Local setup using Docker

The application can be run using Docker. There are two options:

#### Option 1: Using Docker Compose

This option sets up both the application and the PostgreSQL database in Docker containers.

1. Build and start the containers:
   ```
   docker-compose up -d
   ```

2. To stop the containers:
   ```
   docker-compose down
   ```

3. To stop the containers and remove the volumes:
   ```
   docker-compose down -v
   ```

#### Option 2: Using Docker with External Database

This option runs only the application in a Docker container and connects to an external PostgreSQL database.

1. Start an external PostgreSQL instance (if not already running) for example also with Docker:
   ```
   docker run --name field_reservation_db -e POSTGRES_DB=field_reservation -e POSTGRES_USER=field_reservation_db_user -e POSTGRES_PASSWORD=field_reservation_db_password -p 54328:5432 -d postgres:16.2
   ```

2. Build the application:
   ```
   ./gradlew build
   ```

3. Build the Docker image:
   ```
   docker build -t field-reservation-system .
   ```

4. Run the Docker container:
   ```
   docker run -p 8081:8081 -e DB_HOST=host.docker.internal -e DB_PORT=54328 -e SPRING_PROFILES_ACTIVE=local --name field-reservation-system field-reservation-system
   ```

   Note: `host.docker.internal` is a special DNS name that resolves to the host machine from inside the Docker container. This works on Docker Desktop for Windows and Mac. For Linux, you may need to use the host's IP address.

Once the application is running, you can access it at:

- The application runs on port 8081
- GraphQL API endpoint: http://localhost:8081/public/graphql
- GraphiQL (GraphQL IDE): http://localhost:8081/graphiql
- Management endpoints: http://localhost:8081/management/

## Development

### Running Tests

To be able to run integration tests, you need to have Docker running - for example Docker Desktop.
To run the tests, use the following command:

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

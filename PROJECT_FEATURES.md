# Project Features

## 1. REST API
- RESTful endpoints for Main Issues and Defects
- CRUD operations with proper HTTP methods
- JSON request/response format

## 2. OpenAPI/Swagger Documentation
- Interactive API documentation at `/swagger-ui.html`
- Auto-generated from code annotations
- Accessible API specs in JSON and YAML formats

## 3. JWT Authentication
- Spring Security with OAuth2 Resource Server
- RSA-based JWT token validation
- Protected endpoints with method-level security

## 4. Input Validation
- Jakarta Bean Validation on DTOs
- Automatic validation with meaningful error messages
- RFC 7807 Problem Details for errors

## 5. Database Integration
- PostgreSQL with Spring Data JPA
- HikariCP connection pooling (50 max connections, 10 min idle)
- Optimistic locking with version fields

## 6. Liquibase Database Migration
- Version-controlled database changes
- XML changelog files with rollback support
- Automatic migration on startup

## 7. Spring Data JPA
- Repository pattern for data access
- Custom query methods with @Query annotations
- JOIN FETCH for efficient data loading

## 8. Entity Relationships
- One-to-Many between Main Issue and Defects
- Cascade operations for referential integrity
- Abstract base entity with common fields

## 9. DTO Pattern with MapStruct
- Separation between entities and DTOs
- Automatic mapping at compile-time
- Clean API contracts

## 10. Error Handling
- RFC 7807 Problem Details standard
- Custom exception handling
- Proper HTTP status codes

## 11. Integration Testing
- Testcontainers for isolated PostgreSQL
- Full Spring context tests
- Real database and HTTP endpoint testing

## 12. Unit Testing
- JUnit 5 with Mockito
- Service layer tests with mocked dependencies
- Fast isolated tests

## 13. Code Coverage
- JaCoCo integration
- HTML coverage reports in `target/site/jacoco/`
- Metrics per class and package

## 14. External Service Integration
- WebClient for HTTP calls (used synchronously)
- Timeout protection and graceful error handling
- Demo endpoint combining external and database data

## 15. Logging
- SLF4J with Logback
- Structured logging across all layers
- Configurable log levels

## 16. Maven Build System
- Spring Boot Maven Plugin
- Surefire for unit tests, Failsafe for integration tests
- Annotation processors for Lombok and MapStruct

## 17. Lombok Integration
- Reduced boilerplate with annotations
- Generated getters, setters, constructors
- Logger field generation

## 18. Docker Support
- Dockerfile for containerization
- Docker Compose with PostgreSQL
- Java 21 base image

## 19. Security Configuration
- Custom SecurityFilterChain
- CSRF disabled for stateless REST API
- Selective endpoint protection (Swagger/Actuator public)

## 20. Cascade Operations
- Automatic deletion of child entities
- JPA cascade configuration
- Referential integrity maintenance

## 21. Optimistic Locking
- Version field on entities
- Prevents lost updates in concurrent scenarios
- Automatic version management

## 22. N+1 Query Prevention
- JOIN FETCH in custom queries
- Efficient loading of defects with main issues
- Performance optimization via @Query annotations

## 23. Virtual Threads (Java 21)
- Lightweight threads for high concurrency
- Prevents thread starvation with blocking I/O
- Automatic configuration via Spring Boot

## 24. Distributed Tracing with Micrometer
- Automatic trace ID generation for every HTTP request
- MDC (Mapped Diagnostic Context) integration
- Trace ID propagation to external services via HTTP headers
- Thread name and trace ID in console logs
- Spring Boot Actuator for tracing infrastructure

**Features:**
- Unique trace ID per request for request tracking
- Trace ID available in all log statements within a request
- X-Trace-Id header propagation to external services (WebClient)
- W3C Trace Context standard compliance
- Ready for integration with Zipkin/Jaeger

**Log Format:**
```
2026-03-04 11:30:20 [http-nio-8080-exec-1] [traceId:69a809bcf8ddea0559445d506bd09ed5] INFO c.e.demo.rest.DefectRestController - Received GET request
```

---

## Technology Stack

**Core**
- Spring Boot 3.3.4
- Java 21

**Database**
- PostgreSQL
- Spring Data JPA
- Hibernate
- Liquibase

**Security**
- Spring Security
- OAuth2 Resource Server
- JWT

**Documentation**
- SpringDoc OpenAPI
- Swagger UI

**Validation & Mapping**
- MapStruct
- Jakarta Bean Validation
- Hibernate Validator

**HTTP Client**
- Spring WebClient (synchronous mode)

**Observability**
- Micrometer Tracing (Brave)
- Spring Boot Actuator
- SLF4J/Logback with MDC

**Testing**
- JUnit 5
- Mockito
- Testcontainers
- JaCoCo

**Build & Tools**
- Maven
- Lombok
- Docker

---

## Build Commands

**Compile**
```bash
./mvnw clean compile
```

**Run Tests**
```bash
./mvnw test              # Unit tests
./mvnw verify            # Unit + Integration tests
```

**Package**
```bash
./mvnw clean package
./mvnw clean package -DskipTests
```

**Run Application**
```bash
./mvnw spring-boot:run
```

**Code Coverage**
```bash
./mvnw clean verify
# Report: target/site/jacoco/index.html
```

---

## Project Structure

```
src/main/java/com/example/demo/
├── config/              # Configuration classes
├── dto/                 # Data Transfer Objects
│   ├── req/            # Request DTOs
│   ├── resp/           # Response DTOs
│   └── external/       # External service DTOs
├── entities/           # JPA Entities
├── exceptions/         # Custom exceptions
├── mappers/            # MapStruct mappers
├── repositories/       # Spring Data repositories
├── rest/               # REST Controllers
└── services/           # Service layer

src/main/resources/
├── application.properties
└── db/changelog/       # Liquibase migrations

src/test/java/com/example/demo/
├── repositories/       # Integration tests
├── rest/               # Controller tests
├── services/           # Unit tests
└── util/               # Test utilities
```

---

## Design Patterns

**Layered Architecture**
- Presentation Layer (REST Controllers)
- Service Layer (Business Logic)
- Repository Layer (Data Access)
- DTO Layer (Data Transfer)

**Dependency Injection**
- Constructor-based injection via Spring IoC

**Repository Pattern**
- Data access abstraction with Spring Data JPA

**DTO Pattern**
- Separation of API contracts from domain entities

**Service Layer Pattern**
- Business logic orchestration and transaction management


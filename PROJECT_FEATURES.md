# Project Features Documentation

This document provides an overview of all features and technologies implemented in this Spring Boot REST API project.

---

## 1. REST API

### Implementation
- RESTful API design following HTTP standards
- CRUD operations for Main Issues and Defects
- Proper HTTP methods (GET, POST, PUT, DELETE)
- Meaningful HTTP status codes (200, 201, 404, etc.)
- JSON request/response format

### Controllers
- `MainIssueRestController` - Manages main issues
- `DefectRestController` - Manages defects
- Base path: `/api/main-issue` and `/api/defect`

---

## 2. OpenAPI/Swagger Documentation

### Implementation
- SpringDoc OpenAPI 3 integration
- Interactive API documentation via Swagger UI
- Automatic schema generation from code
- Detailed operation descriptions with examples

### Access Points
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- OpenAPI YAML: `http://localhost:8080/api-docs.yaml`

### Configuration
- Custom API info (title, version, description)
- Configured in `OpenApiConfig.java`

---

## 3. JWT Authentication

### Implementation
- Spring Security OAuth2 Resource Server
- RSA-based JWT signature validation
- Public key configuration via properties
- Audience and issuer validation

### Security Rules
- POST `/api/main-issue` requires JWT authentication
- Other endpoints are publicly accessible
- Method-level security using `@PreAuthorize`

### Token Validation
- Custom JWT audience validator
- RSA public key verification
- Token expiration checking

---

## 4. Input Validation

### Implementation
- Jakarta Bean Validation (JSR-380)
- Hibernate Validator
- Annotations on DTO classes (`@NotNull`, `@NotBlank`, etc.)

### Validation Rules
- Request DTOs validated automatically
- Custom validation messages
- Validation errors returned as Problem Details (RFC 7807)

---

## 5. Database Integration

### PostgreSQL
- Production database: PostgreSQL
- Connection pooling via HikariCP
- JPA/Hibernate for ORM
- Optimistic locking with `@Version`

### Configuration
- Connection settings in `application.properties`
- Schema: `demo`
- Automatic schema validation

---

## 6. Liquibase Database Migration

### Implementation
- Version-controlled database changes
- Changelog files in XML format
- Automatic migration on application startup

### Location
- Master changelog: `db/changelog/db.changelog-master.xml`
- Changesets organized by date (e.g., `2024-10/`)

### Features
- Table creation (main_issue, defect)
- Schema versioning
- Rollback capability

---

## 7. Spring Data JPA

### Repositories
- `MainIssueRepository` - Main issue data access
- `DefectRepository` - Defect data access
- Custom query methods
- Batch operations for efficiency

### Features
- Automatic CRUD operations
- Custom query methods (e.g., `findByMainIssueIdIn`)
- Entity relationships (OneToMany, ManyToOne)
- Cascade operations

---

## 8. Entity Relationships

### Main Issue - Defect Relationship
- One-to-Many: One Main Issue can have multiple Defects
- Cascade deletion: Deleting Main Issue deletes associated Defects
- Bidirectional mapping with `@ManyToOne` and `@OneToMany`

### Abstract Entity
- Base entity class with common fields
- ID generation (UUID)
- Audit fields (created, modified)
- Version field for optimistic locking

---

## 9. DTO Pattern with MapStruct

### Implementation
- Separation of entity and DTO layers
- Automatic mapping via MapStruct
- Compile-time code generation

### Mappers
- `MainIssueMapper` - Entity to/from DTO conversion
- `DefectMapper` - Entity to/from DTO conversion

### DTOs
- Request DTOs: `MainIssueReqDto`, `DefectReqDto`
- Response DTOs: `MainIssueRespDto`, `DefectRespDto`

---

## 10. Error Handling

### RFC 7807 Problem Details
- Standardized error responses
- HTTP status codes with detailed problem information
- Automatic exception handling

### Exception Types
- `EntityNotFoundException` - Returns 404
- Validation errors - Returns 400
- Security errors - Returns 401/403

---

## 11. Integration Testing

### Testcontainers
- PostgreSQL container for tests
- Isolated test database
- Automatic container lifecycle management

### Test Coverage
- `MainIssueRepositoryTestIT` - Repository layer tests
- `MainIssueControllerTestIT` - API endpoint tests
- `DefectControllerTestIT` - Defect API tests

### Features
- Full Spring context loading
- Real database interactions
- HTTP endpoint testing with MockMvc

---

## 12. Unit Testing

### Implementation
- JUnit 5 (Jupiter)
- Mockito for mocking
- Service layer tests with mocked dependencies

### Test Classes
- `MainIssueServiceImplTest` - Service layer unit tests
- `ExternalServiceClientTest` - External service client tests

### Coverage
- Mock repositories and dependencies
- Isolated unit testing
- Fast execution

---

## 13. Code Coverage

### JaCoCo Integration
- Code coverage analysis
- HTML reports generation
- Execution tracking

### Reports
- Location: `target/site/jacoco/index.html`
- Generated during verify phase
- Coverage metrics per class and package

---

## 14. External Service Integration

### WebClient Implementation
- Spring WebFlux WebClient for HTTP calls
- Reactive HTTP client
- Non-blocking I/O

### External Service Client
- Calls external REST APIs
- Timeout protection (5 seconds)
- Graceful error handling (returns null on failure)

### Demo Endpoint
- GET `/api/defect/{id}` demonstrates:
  - Step 1: Call external service
  - Step 2: Query database
  - Step 3: Combine results

### Configuration
- Base URL: Configurable via properties
- Timeouts: Connection and read timeouts
- Resilience: Application continues if external service fails

---

## 15. Logging

### SLF4J with Logback
- Structured logging throughout application
- Different log levels (INFO, DEBUG, WARN, ERROR)
- Request/response logging in controllers

### Log Messages
- Service layer operations
- External service calls
- Database operations
- Security events

---

## 16. Configuration Management

### Externalized Configuration
- `application.properties` for all settings
- Database connection parameters
- JWT security settings
- External service configuration
- Logging levels

### Profiles Support
- Spring profiles for different environments
- Profile-specific configurations possible

---

## 17. Maven Build System

### Build Configuration
- Spring Boot Maven Plugin
- Compiler plugin with annotation processors
- Surefire plugin for unit tests
- Failsafe plugin for integration tests

### Annotation Processors
- Lombok for boilerplate reduction
- MapStruct for DTO mapping
- Lombok-MapStruct binding for compatibility

---

## 18. Lombok Integration

### Features Used
- `@Data` - Generates getters, setters, toString, equals, hashCode
- `@RequiredArgsConstructor` - Constructor injection
- `@Slf4j` - Logger field generation
- `@NoArgsConstructor`, `@AllArgsConstructor`

### Benefits
- Reduced boilerplate code
- Cleaner, more maintainable code
- Compile-time code generation

---

## 19. Docker Support

### Dockerfile
- Multi-stage build support
- Java 21 runtime
- Optimized image size

### Docker Compose
- Application + PostgreSQL setup
- Network configuration
- Volume management
- Environment variables

---

## 20. Security Configuration

### Custom Security Config
- SecurityFilterChain configuration
- JWT token validation
- CORS configuration
- CSRF protection settings

### Public Endpoints
- Most endpoints accessible without authentication
- Selective protection on sensitive operations

---

## 21. API Versioning Consideration

### Current Approach
- Version in base path (`/api/...`)
- Prepared for future versioning needs

---

## 22. Data Transfer Objects

### Request DTOs
- Validate incoming data
- Decoupled from entities
- Clean API contracts

### Response DTOs
- Control exposed data
- Include computed fields
- API stability

---

## 23. Cascade Operations

### Implementation
- Main Issue deletion cascades to Defects
- Configured via JPA `@OneToMany(cascade = CascadeType.ALL)`
- Maintains referential integrity

---

## 24. Optimistic Locking

### Implementation
- `@Version` annotation on entities
- Prevents lost updates
- Automatic version management by JPA

---

## 25. UUID Primary Keys

### Benefits
- Globally unique identifiers
- No database sequences needed
- Better for distributed systems
- Secure (non-sequential)

---

## 26. Audit Fields

### Implementation
- Created timestamp
- Modified timestamp
- Automatically managed in AbstractEntity

---

## 27. Test Utilities

### JWT Token Generator
- Generate test JWT tokens
- Used for testing authenticated endpoints
- Located in `src/test/java/com/example/demo/util/`

### Mock Bean Generator
- Generates test data
- Simplifies test setup

---

## 28. N+1 Query Prevention

### Implementation
- Batch fetching in `getDefectsByMainIssueIds()`
- Single query instead of N queries
- Performance optimization demonstrated in service layer

---

## Technology Stack Summary

**Core Framework**
- Spring Boot 3.3.4
- Java 21

**Database**
- PostgreSQL
- Spring Data JPA
- Hibernate ORM
- Liquibase

**Security**
- Spring Security
- OAuth2 Resource Server
- JWT with RSA validation

**Documentation**
- SpringDoc OpenAPI
- Swagger UI

**Mapping & Validation**
- MapStruct
- Jakarta Bean Validation
- Hibernate Validator

**HTTP Client**
- Spring WebFlux WebClient

**Testing**
- JUnit 5
- Mockito
- Testcontainers
- JaCoCo

**Build & Tools**
- Maven
- Lombok
- Docker

**Logging**
- SLF4J
- Logback

---

## Build Commands

### Compile
```bash
./mvnw clean compile
```

### Run Tests
```bash
./mvnw test                    # Unit tests only
./mvnw verify                  # Unit + Integration tests
./mvnw test -Dtest=ClassName   # Specific test
```

### Package
```bash
./mvnw clean package
```

### Run Application
```bash
./mvnw spring-boot:run
```

### Skip Tests
```bash
./mvnw clean package -DskipTests
```

### Code Coverage Report
```bash
./mvnw clean verify
# Report: target/site/jacoco/index.html
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Configuration classes
│   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── req/         # Request DTOs
│   │   │   ├── resp/        # Response DTOs
│   │   │   └── external/    # External service DTOs
│   │   ├── entities/        # JPA Entities
│   │   ├── exceptions/      # Custom exceptions
│   │   ├── mappers/         # MapStruct mappers
│   │   ├── repositories/    # Spring Data repositories
│   │   ├── rest/            # REST Controllers
│   │   └── services/        # Service layer
│   └── resources/
│       ├── application.properties
│       └── db/changelog/    # Liquibase migrations
└── test/
    └── java/com/example/demo/
        ├── repositories/    # Integration tests
        ├── rest/            # Controller tests
        ├── services/        # Unit tests
        └── util/            # Test utilities
```

---

## Key Design Patterns

### Layered Architecture
- Presentation Layer (Controllers)
- Service Layer (Business Logic)
- Repository Layer (Data Access)
- DTO Layer (Data Transfer)

### Dependency Injection
- Constructor-based injection
- Spring IoC container management

### Repository Pattern
- Spring Data JPA repositories
- Abstract data access logic

### DTO Pattern
- Separation of concerns
- API contract stability

### Service Facade
- Complex operations orchestration
- Transaction management

---

## Best Practices Implemented

### Code Quality
- Clean separation of layers
- DRY principle
- SOLID principles
- Meaningful naming conventions

### Security
- JWT authentication
- Input validation
- SQL injection prevention (JPA)
- Prepared statements

### Performance
- Connection pooling
- N+1 query prevention
- Optimistic locking
- Batch operations

### Maintainability
- Comprehensive logging
- Error handling
- Documentation
- Test coverage

### Resilience
- Graceful degradation (external services)
- Timeout protection
- Transaction management
- Cascade operations

---

This document provides a complete overview of all features and technologies implemented in this project.


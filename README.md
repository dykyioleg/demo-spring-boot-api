# Demo Project - Spring Boot REST API

A Spring Boot REST API demonstrating CRUD operations, JWT authentication, and modern Java development practices.

## Features

- ✅ RESTful API with CRUD operations for Main Issues and Defects
- ✅ JWT Authentication with RSA signature validation
- ✅ Method-level security using `@PreAuthorize`
- ✅ OpenAPI/Swagger documentation
- ✅ Cascade deletion (Main Issue → Defects)
- ✅ Input validation with Jakarta Bean Validation
- ✅ RFC 7807 Problem Details for errors
- ✅ Integration tests with Testcontainers
- ✅ MapStruct for DTO mapping
- ✅ Liquibase for database migrations

## Technology Stack

- **Java**: 21
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security OAuth2 Resource Server
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Migration**: Liquibase
- **Mapping**: MapStruct
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Testcontainers

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL (or Docker for Testcontainers)
- Docker (for integration tests)

## Quick Start

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd demo
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/demo
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```

Application starts on: http://localhost:8080

### 4. Access API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## API Endpoints

### Main Issue APIs

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/api/main-issue/{id}` | No | Get main issue by ID |
| POST | `/api/main-issue` | **Yes (JWT)** | Create new main issue |
| PUT | `/api/main-issue/{id}` | No | Update main issue |
| DELETE | `/api/main-issue/{id}` | No | Delete main issue (cascade) |

### Defect APIs

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/api/defect/{id}` | No | Get defect by ID |
| POST | `/api/defect` | No | Create new defect |
| PUT | `/api/defect/{id}` | No | Update defect |
| DELETE | `/api/defect/{id}` | No | Delete defect |

## JWT Authentication

Only **POST /api/main-issue** requires JWT authentication.

### Generate Test Token

Run the token generator:
```bash
# From your IDE, run the main method in:
src/test/java/com/example/demo/util/JwtTestTokenGenerator.java
```

### Use Token in Postman/cURL

```bash
curl -X POST http://localhost:8080/api/main-issue \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -d '{"description": "Test issue", "reportable": true}'
```

## Testing

### Run all tests
```bash
./mvnw verify
```

### Run unit tests only
```bash
./mvnw test
```

### Run specific test
```bash
./mvnw test -Dtest=MainIssueControllerTestIT
```

**Test Results**: 15 tests, all passing ✓

## Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Security, OpenAPI, MapStruct config
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entities/        # JPA entities
│   │   ├── mappers/         # MapStruct mappers
│   │   ├── repositories/    # Spring Data JPA repositories
│   │   ├── rest/            # REST controllers
│   │   └── services/        # Business logic services
│   └── resources/
│       ├── application.properties
│       └── db/changelog/    # Liquibase migrations
└── test/
    ├── java/com/example/demo/
    │   ├── config/          # Test security configuration
    │   ├── repositories/    # Repository integration tests
    │   ├── rest/            # Controller integration tests
    │   ├── services/        # Service unit tests
    │   └── util/            # JWT token generator
    └── resources/
```

## Configuration

### JWT Settings (application.properties)
```properties
jwt.issuer=https://demo.example.com
jwt.audience=demo-api
jwt.public-key=<RSA_PUBLIC_KEY>
```

### Security Profiles
- **Production** (default): Full JWT security active
- **Test** (profile=test): Security disabled for integration tests

## Documentation

- **API_DOCS.md** - Complete API documentation
- **SECURITY_EXPLAINED.md** - Security architecture and how it works
- **QUICK_REFERENCE.md** - Quick commands and examples

## Security Architecture

- **HTTP-level**: All `/api/**` endpoints permitted
- **Method-level**: Only `POST /api/main-issue` requires JWT (via `@PreAuthorize`)
- **JWT Validation**: RSA signature, issuer, audience, expiration checks
- **Test Isolation**: Security disabled in test profile

## Development

### Build the project
```bash
./mvnw clean install
```

### Run with different profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Generate code coverage report
```bash
./mvnw verify
# Report: target/site/jacoco/index.html
```

## Docker Support

### Build Docker image
```bash
docker build -t demo-api .
```

### Run with Docker Compose
```bash
docker-compose up
```

## Contributing

This is a demo project for interview purposes. Not accepting contributions.

## License

This project is for demonstration purposes only.

## Contact

For questions or feedback, please contact the project maintainer.

---

**Built with ❤️ using Spring Boot**


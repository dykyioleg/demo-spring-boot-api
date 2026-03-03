# OpenAPI/Swagger Documentation

## How to Access

### Start the application:
```bash
./mvnw spring-boot:run
```

### Access Swagger UI (Interactive Documentation):
```
http://localhost:8080/swagger-ui.html
```

### Access OpenAPI Specification:
```
JSON: http://localhost:8080/api-docs
YAML: http://localhost:8080/api-docs.yaml
```

## Available Endpoints

### Main Issue APIs:
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/main-issue/{id}` | Get main issue by ID |
| POST | `/api/main-issue` | Create new main issue |
| PUT | `/api/main-issue/{id}` | Update main issue |
| DELETE | `/api/main-issue/{id}` | Delete main issue (cascade) |

### Defect APIs:
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/defect/{id}` | Get defect by ID (**DEMO**: includes external service call) |
| POST | `/api/defect` | Create new defect |
| PUT | `/api/defect/{id}` | Update defect |
| DELETE | `/api/defect/{id}` | Delete defect |

## Configuration

To customize API documentation, edit: `src/main/java/com/example/demo/config/OpenApiConfig.java`



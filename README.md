# Student Management System (SMS)

A Spring Boot REST API for managing student records with validation, error handling, and Swagger UI documentation.

## Project Overview

This application provides CRUD operations for student management with the following features:

- **Spring Boot 3.5.7** with Spring Data JPA
- **H2 in-memory database** for development
- **Validation** on entity fields (email format, age > 18, required fields)
- **Global Exception Handling** with proper HTTP status codes
- **Swagger UI** for interactive API testing
- **Service Layer** architecture (no direct Controller → Repository calls)

## Prerequisites

- Java 17+
- Maven 3.6+

## Project Structure

```
src/main/java/com/usj/sms/
├── SmsApplication.java           # Main Spring Boot application
├── controller/
│   └── StudentController.java     # REST endpoints
├── service/
│   └── StudentService.java        # Business logic layer
├── repository/
│   └── StudentRepository.java     # JPA repository (CRUD)
├── entity/
│   └── Student.java               # Entity with validation annotations
└── exception/
    └── GlobalExceptionHandler.java # Global exception handling

src/main/resources/
└── application.properties          # Database and Swagger configuration
```

## How to Run

### 1. Build the project

```bash
mvnw.cmd -DskipTests clean package
```

### 2. Run the application

```bash
mvnw.cmd spring-boot:run
```

The application will start on **http://localhost:8080**

## Docker (containerize the app)

Why Docker?
- Docker packages your application and its runtime into a portable container image. That image can run the same way on any machine with Docker installed. This avoids "it works on my machine" problems and makes deployments reproducible.

What I added
- `Dockerfile` — multi-stage build that compiles the project with Maven then copies the resulting jar into a small JRE image.
- `.dockerignore` — excludes build artifacts and large folders from the build context.

Build and run with Docker (requires Docker installed)

1. Build the image (run from project root):

```powershell
docker build -t sms-app:latest .
```

2. Run the container and map port 8080:

```powershell
docker run --rm -p 8080:8080 --name sms-app sms-app:latest
```

3. Verify the API (open in Postman or browser):

```
http://localhost:8080/api/students
http://localhost:8080/h2-console
```

Notes
- If you change the project artifactId or version in `pom.xml`, update the `ARG JAR_FILE` in the `Dockerfile` accordingly.
- Use `docker logs -f sms-app` to follow container logs.
- Use `docker run -e SPRING_PROFILES_ACTIVE=prod ...` to set environment variables.


### 3. Access Swagger UI

Open your browser and navigate to:
- **Swagger UI (interactive API testing)**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON (API spec)**: http://localhost:8080/v3/api-docs
- **H2 Console (database admin)**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - User: `sa`
  - Password: (leave blank)

## API Endpoints

All endpoints are prefixed with `/api/students`

### 1. Create Student (POST)

```http
POST /api/students
Content-Type: application/json

{
  "name": "Alice",
  "email": "alice@test.com",
  "course": "ICT",
  "age": 20
}
```

**Expected Response**: `201 Created`

### 2. Get All Students (GET)

```http
GET /api/students
```

You can request paginated and sorted results using query parameters:

- `page` (0-based page index)
- `size` (page size)
- `sort` (format: property,dir e.g. `name,desc`). Can be repeated for multiple sort orders.
y- `name` (optional) — search students whose name contains this value (case-insensitive)
- `course` (optional) — search students whose course contains this value (case-insensitive)

Examples:

```http
GET /api/students?page=0&size=5
GET /api/students?page=0&size=10&sort=name,desc
GET /api/students?name=alice&page=0&size=10
GET /api/students?course=ICT&page=0&size=10
GET /api/students?name=alice&course=ICT&page=0&size=10  # returns students matching BOTH name and course (AND)
```

**Expected Response**: `200 OK` with a JSON Page object containing content, pageable, totalElements, totalPages, etc.

### 3. Get Student by ID (GET)

```http
GET /api/students/1
```

**Expected Response**: `200 OK` (if exists) or `404 Not Found`

### 4. Update Student (PUT)

```http
PUT /api/students/1
Content-Type: application/json

{
  "name": "Alice Updated",
  "email": "alice.updated@test.com",
  "course": "ICT",
  "age": 21
}
```

**Expected Response**: `200 OK` or `404 Not Found`

### 5. Delete Student (DELETE)

```http
DELETE /api/students/1
```

**Expected Response**: `204 No Content`

### 6. Test Validation (POST with invalid data)

```http
POST /api/students
Content-Type: application/json

{
  "name": "",
  "email": "invalid-email",
  "course": "ICT",
  "age": 17
}
```

**Expected Response**: `400 Bad Request`

```json
{
  "name": "Name is mandatory",
  "email": "Email should be a valid format",
  "age": "Student must be at least 18 years old"
}
```

## Testing with curl (Windows cmd)

### Get all students
```cmd
curl "http://localhost:8080/api/students"

# Paginated (first page, 5 per page)
curl "http://localhost:8080/api/students?page=0&size=5"

# Sorted by name descending
curl "http://localhost:8080/api/students?page=0&size=10&sort=name,desc"
```

### Create a student
```cmd
curl -X POST http://localhost:8080/api/students ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Alice\",\"email\":\"alice@test.com\",\"course\":\"ICT\",\"age\":20}"
```

### Get student by ID
```cmd
curl http://localhost:8080/api/students/1
```

### Update student
```cmd
curl -X PUT http://localhost:8080/api/students/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Alice Updated\",\"email\":\"alice.updated@test.com\",\"course\":\"ICT\",\"age\":21}"
```

### Delete student
```cmd
curl -X DELETE http://localhost:8080/api/students/1
```

### Test validation (invalid data)
```cmd
curl -X POST http://localhost:8080/api/students ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"\",\"email\":\"invalid\",\"course\":\"ICT\",\"age\":17}"
```

## Testing with Postman

1. Open **Postman** or **VS Code REST Client**
2. Use the endpoints listed above
3. Set headers: `Content-Type: application/json`
4. Test with valid and invalid data

## Student Entity Validation Rules

- **name**: Required, non-blank
- **email**: Required, must be valid email format
- **course**: Required, non-blank
- **age**: Required, must be ≥ 18 years old

## Global Exception Handling

The application handles two main error scenarios:

1. **Validation Errors** (e.g., age < 18, blank fields)
   - Status: `400 Bad Request`
   - Response: JSON map of field names → error messages

2. **Not Found Errors** (e.g., deleting non-existent student)
   - Status: `404 Not Found`
   - Response: "Resource not found or already deleted."

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Database**: H2 (in-memory)
- **ORM**: Hibernate / Spring Data JPA
- **Validation**: Jakarta Bean Validation
- **API Documentation**: Swagger/OpenAPI (springdoc-openapi 1.7.0)
- **Build Tool**: Maven
- **Java Version**: 17

## Configuration

Key properties in `application.properties`:

```properties
spring.application.name=sms

# H2 database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# Swagger UI
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
```

## Troubleshooting

### Port already in use
If port 8080 is in use, change it in `application.properties`:
```properties
server.port=8081
```

### Database connection errors
The H2 database is auto-configured and should work out of the box. If issues persist, check logs for datasource errors.

### Swagger UI not loading
If you see "Failed to load API definition", ensure:
1. The app is running (check logs for "Started SmsApplication")
2. Hit `http://localhost:8080/swagger-ui.html` (not `/swagger-ui/`)
3. API docs should be available at `/v3/api-docs`

## License

This project is for educational purposes.

## Author

Student Management System Project - November 2025

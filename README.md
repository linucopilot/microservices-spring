# PRASAC Bank Microservices

Complete microservices architecture with JWT authentication, Spring Cloud Gateway, MyBatis CRUD operations, and Docker support.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│              API Gateway (Port 8085)                    │
│  - JWT Token Validation                                 │
│  - Route Management                                      │
│  - Error Handling                                        │
└──────────┬──────────────────────┬──────────────────────┘
           │                      │
      ┌────▼────┐          ┌──────▼──────┐
      │Auth     │          │User Service │
      │Service  │          │(Port 8081)  │
      │(Port    │          │             │
      │8080)    │          │- MyBatis    │
      │         │          │- CRUD Ops   │
      │- JWT    │          │- PostgreSQL │
      │- Login  │          │             │
      │- Reg.   │          └─────────────┘
      └────┬────┘
           │
      ┌────▼────────┐
      │ PostgreSQL  │
      │ (Port 5432) │
      └─────────────┘
```

## Features

✅ **JWT Authentication** - Complete login/validation flow
✅ **Spring Cloud Gateway** - Intelligent routing with JWT filters
✅ **MyBatis XML CRUD** - Full database operations
✅ **ApiResponse Wrapper** - Consistent API responses across all services
✅ **Global Exception Handling** - Centralized error management
✅ **Docker Support** - Containerized services with docker-compose
✅ **PostgreSQL** - Persistent data storage

## Project Structure

```
microservices-spring/
├── pom.xml (Parent)
├── docker-compose.yml
├── init.sql
├── Dockerfile (example)
│
├── auth-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/com/prasac/
│       │   ├── authservice/
│       │   │   ├── AuthServiceApplication.java
│       │   │   ├── controller/AuthController.java
│       │   │   ├── service/AuthService.java
│       │   │   ├── entity/User.java
│       │   │   ├── repository/UserRepository.java
│       │   │   ├── security/JwtTokenProvider.java
│       │   │   └── exception/GlobalExceptionHandler.java
│       │   └── common/dto/ApiResponse.java
│       └── resources/application.yml
│
├── user-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/com/prasac/
│       │   ├── userservice/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── controller/UserController.java
│       │   │   ├── service/UserService.java
│       │   │   ├── entity/User.java
│       │   │   ├── mapper/UserMapper.java
│       │   │   └── exception/GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           └── mapper/UserMapper.xml
│
└── gateway/
    ├── pom.xml
    ├── Dockerfile
    └── src/main/
        ├── java/com/prasac/
        │   └── gateway/
        │       ├── GatewayApplication.java
        │       └── filter/JwtAuthenticationFilter.java
        └── resources/application.yml
```

## Prerequisites

- **Docker** & **Docker Compose**
- **Java 17+** (for local development)
- **Maven 3.8+**
- **PostgreSQL 15** (included in docker-compose)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# 1. Clone and navigate to project
cd /path/to/microservices-spring

# 2. Build and run all services
docker-compose up --build

# 3. Services will be available at:
# - API Gateway: http://localhost:8085
# - Auth Service: http://localhost:8080
# - User Service: http://localhost:8081
# - PostgreSQL: localhost:5432
```

### Option 2: Build Locally

```bash
# 1. Build the entire project
mvn clean package -DskipTests

# 2. Run PostgreSQL
docker run -d \
  --name prasac-postgres \
  -e POSTGRES_DB=prasac \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# 3. Run services (in separate terminals)
# Terminal 1 - Auth Service
java -jar auth-service/target/auth-service-1.0.0.jar

# Terminal 2 - User Service
java -jar user-service/target/user-service-1.0.0.jar

# Terminal 3 - Gateway
java -jar gateway/target/gateway-1.0.0.jar
```

## API Usage

### 1. Register a New User

```bash
curl -X POST http://localhost:8085/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'
```

**Response:**
```json
{
  "code": 200,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe"
  },
  "timestamp": 1234567890
}
```

### 2. Login

```bash
curl -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "johndoe",
    "userId": 1,
    "email": "john@example.com"
  },
  "timestamp": 1234567890
}
```

### 3. Create User Profile (User Service)

```bash
curl -X POST http://localhost:8085/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phone": "+855-12-345-678",
    "address": "Phnom Penh, Cambodia"
  }'
```

### 4. Get All Users

```bash
curl -X GET http://localhost:8085/api/users \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 5. Get User by ID

```bash
curl -X GET http://localhost:8085/api/users/1 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 6. Update User

```bash
curl -X PUT http://localhost:8085/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "username": "johndoe",
    "email": "john.new@example.com",
    "fullName": "John Doe Updated",
    "phone": "+855-98-765-432",
    "address": "Siem Reap, Cambodia"
  }'
```

### 7. Delete User

```bash
curl -X DELETE http://localhost:8085/api/users/1 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 8. Validate Token

```bash
curl -X GET http://localhost:8085/api/auth/validate \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Test Credentials

Default user created during database initialization:
- **Username:** admin
- **Password:** (use any password initially, then register/login with your own)
- **Email:** admin@prasac.com

## Configuration

### JWT Settings

Update the following in each service's `application.yml`:

```yaml
jwt:
  secret: "your-super-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm-change-this"
  expiration: 86400000  # 24 hours in milliseconds
```

### Database Connection

PostgreSQL connection details (can be overridden via environment variables):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/prasac
    username: postgres
    password: postgres
```

## Development

### Build Only

```bash
mvn clean package -DskipTests
```

### Build with Tests

```bash
mvn clean package
```

### Run with Maven

```bash
# Auth Service
cd auth-service && mvn spring-boot:run

# User Service
cd user-service && mvn spring-boot:run

# Gateway
cd gateway && mvn spring-boot:run
```

## Troubleshooting

### Containers won't start

```bash
# Check logs
docker-compose logs -f

# Restart all services
docker-compose restart

# Clean rebuild
docker-compose down -v
docker-compose up --build
```

### JWT Token Issues

- Ensure `JWT_SECRET` is the same across all services
- Check token expiration: default is 24 hours
- Validate token format: Must include "Bearer " prefix

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# View PostgreSQL logs
docker logs prasac-postgres

# Connect to database manually
docker exec -it prasac-postgres psql -U postgres -d prasac
```

### Port Already in Use

```bash
# Find and kill process on port
lsof -i :8085  # Gateway
lsof -i :8080  # Auth Service
lsof -i :8081  # User Service
lsof -i :5432  # PostgreSQL

kill -9 <PID>
```

## Technology Stack

- **Framework:** Spring Boot 3.1.5
- **Cloud:** Spring Cloud 2022.0.4
- **Gateway:** Spring Cloud Gateway
- **Database:** PostgreSQL 15
- **ORM:** MyBatis 3.0.2
- **Security:** Spring Security + JWT (JJWT 0.12.3)
- **Java Version:** 17
- **Build Tool:** Maven 3.8.6

## API Response Format

All API responses follow a consistent format:

```json
{
  "code": 200,
  "message": "Operation successful",
  "data": { /* actual data */ },
  "timestamp": 1234567890
}
```

### Error Response

```json
{
  "code": 400,
  "message": "Error description",
  "data": null,
  "timestamp": 1234567890
}
```

## Security Notes

⚠️ **Important:** Before deploying to production:

1. Change the JWT secret to a strong, random value
2. Use HTTPS/TLS for all communications
3. Implement rate limiting on authentication endpoints
4. Add API key authentication for service-to-service communication
5. Implement proper CORS policies
6. Use environment variables for sensitive configurations
7. Enable database connection pooling
8. Add request/response logging and monitoring
9. Implement proper input validation
10. Set up proper backup strategies for PostgreSQL

## License

This project is for PRASAC Bank use only.

## Support

For issues or questions, contact the development team.

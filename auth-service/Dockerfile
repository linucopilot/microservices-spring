# Build stage
FROM maven:3.9.11-amazoncorretto-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY auth-service auth-service
COPY user-service user-service
COPY gateway gateway
RUN mvn clean package -DskipTests

# Auth Service Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /build/auth-service/target/auth-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# ===================================================================
# Stage 1: Build Stage
# ===================================================================
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

# Copy Maven Wrapper and POM first to leverage Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Grant execution rights to the Maven wrapper
RUN chmod +x mvnw

# Download dependencies in offline mode
RUN ./mvnw dependency:go-offline -B

# Copy application source code
COPY src ./src

# Package application into executable JAR (skipping unit tests for fast builds)
RUN ./mvnw clean package -DskipTests

# ===================================================================
# Stage 2: Minimal Runtime Stage
# ===================================================================
FROM eclipse-temurin:17-jre-jammy AS runner

# Create a secure non-root user and group
RUN groupadd -r datahub && useradd -r -g datahub datahub

WORKDIR /app

# Copy the compiled JAR artifact from builder stage
COPY --from=builder /build/target/data-hub-0.0.1-SNAPSHOT.jar app.jar

# Set ownership to non-root user
RUN chown -R datahub:datahub /app

USER datahub

# Default environment variables
ENV PORT=5000
ENV APP_SEED=false

# Expose server port
EXPOSE 5000

# Health check probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:${PORT}/health || exit 1

# Execute Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]

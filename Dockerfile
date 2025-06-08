# syntax=docker/dockerfile:1
# Multi-stage build for Spring Boot application

# Build stage with slim image
FROM gradle:8.14-jdk21-alpine AS builder

WORKDIR /app

# Enable BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=cache,target=/root/.gradle/wrapper \
    echo "BuildKit cache enabled"

# Copy dependency files first for better cache utilization
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# Download dependencies with cache
RUN --mount=type=cache,target=/root/.gradle \
    gradle dependencies --no-daemon --parallel

# Copy source code
COPY src ./src

# Build application with cache
RUN --mount=type=cache,target=/root/.gradle \
    gradle build -x test --no-daemon --parallel && \
    # Extract layers for optimized runtime
    cd build/libs && \
    java -Djarmode=layertools -jar *.jar extract

# Runtime stage - Using JRE alpine for smaller size
FROM eclipse-temurin:21-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Create non-root user
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app

# Copy layers from builder (order matters for caching)
COPY --from=builder --chown=spring:spring /app/build/libs/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/build/libs/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/build/libs/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/build/libs/application/ ./

USER spring:spring

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:InitialRAMPercentage=50.0 \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+UseStringDeduplication \
    -XX:+ParallelRefProcEnabled \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

EXPOSE 8882

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
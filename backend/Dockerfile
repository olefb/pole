# ------------------
# BUILDING FRONTEND
# ------------------
FROM node:22-alpine AS frontend_builder

WORKDIR /app/frontend

# Paths are relative to the build context (project root)
COPY frontend/package*.json ./
COPY frontend/vite.config.js ./

# Install dependencies
RUN npm install

# Copy source code
COPY frontend/index.html ./
COPY frontend/src ./src

# Run the build command
RUN npm run build

# ------------------
# BUILDING BACKEND
# ------------------
FROM gradle:9.1.0-jdk25-alpine AS builder

WORKDIR /app/backend

# Paths are relative to the build context (project root)
COPY backend/gradlew backend/settings.gradle* backend/build.gradle* ./
COPY backend/gradle ./gradle
COPY backend/src ./src

COPY --from=frontend_builder /app/frontend/dist ./src/main/resources/static

RUN gradle bootJar --no-daemon
RUN find build/libs/ -name "*.jar" -exec mv {} /app.jar \;

# ------------------
# RUNNING
# ------------------
FROM eclipse-temurin:25-alpine

# Set timezone
ENV TZ=UTC

# Create a non-root user and group
RUN addgroup -g 1000 app && adduser -G app -D -u 1000 -h /app app

# Switch to the new user and set the working directory
USER app
WORKDIR /app

# Copy the built JAR from the builder stage
# Chown ensures the 'app' user owns the file
COPY --from=builder --chown=1000:1000 /app.jar .

# COPY --chown=1000:1000 backend/storage/app.mv.db /app/storage/app.mv.db

EXPOSE 8080

# Configure memory limits for the JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Start the application
CMD ["java", "-jar", "app.jar"]

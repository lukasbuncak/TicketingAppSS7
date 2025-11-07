# ---- Build stage (Gradle wrapper + JDK 17) ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy wrapper & config first to leverage Docker cache
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Ensure wrapper is executable (important on Windows)
RUN chmod +x gradlew

# (Optional) warm up deps for faster rebuilds
RUN ./gradlew --no-daemon help || true

# Copy sources and build fat JAR
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test \
    && cp build/libs/*.jar app.jar   # normalize the jar name for next stage

# ---- Runtime stage (lightweight JRE 17) ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create non-root user for safer runtime
RUN useradd --no-create-home --uid 10001 appuser

# Bring in the app JAR produced in the build stage
COPY --from=build /app/app.jar /app/app.jar

# Document the app port (use --target-port 8080 in Azure)
EXPOSE 8080

# Drop root
USER 10001

# Start the app
ENTRYPOINT ["java","-jar","/app/app.jar"]

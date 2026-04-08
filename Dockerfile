# Stage 1: Build the app using your project's Gradle Wrapper
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . .

# Fix Windows line endings (just in case) and grant execution rights
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

# Build the project WITHOUT the daemon to prevent memory crashes
RUN ./gradlew build -x test --no-daemon --info --stacktrace

# Stage 2: Run the app using the ultra-lightweight Alpine JRE
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

# Boot up with the 300MB RAM safety limit
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
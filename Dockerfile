# Stage 1: Build the app using your project's Gradle Wrapper
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . .

# Grant execution rights to the wrapper script
RUN chmod +x ./gradlew

# Build the project (The wrapper will automatically download Gradle 9.3.0)
RUN ./gradlew build -x test

# Stage 2: Run the app using the ultra-lightweight Alpine JRE
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

# Boot up with the 300MB RAM safety limit
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
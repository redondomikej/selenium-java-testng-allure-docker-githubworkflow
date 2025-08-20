# Stage 1: Build the project
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run tests
FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app

# Copy built project from Stage 1
COPY --from=build /app /app

# Environment variables for headless / CI
ENV HEADLESS=true
ENV SELENIUM_URL=http://selenium:4444/wd/hub

# Run Maven tests
CMD ["mvn", "test"]

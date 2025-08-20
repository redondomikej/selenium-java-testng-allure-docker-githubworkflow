# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run tests
FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app
COPY --from=build /app /app
CMD ["mvn", "test"]

# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run with Maven
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app


# Install Google Chrome + dependencies
RUN apt-get update && apt-get install -y wget curl unzip gnupg \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y \
        google-chrome-stable \
        fonts-liberation \
        libappindicator3-1 \
        libasound2 \
        libatk-bridge2.0-0 \
        libatk1.0-0 \
        libcups2 \
        libdbus-1-3 \
        libgdk-pixbuf2.0-0 \
        libnspr4 \
        libnss3 \
        libu2f-udev \
        libvulkan1 \
        xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# Copy compiled project
COPY --from=build /app /app

# Run tests
CMD ["mvn", "test"]
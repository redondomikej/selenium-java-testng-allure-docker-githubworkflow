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

# Install Chrome + Chromedriver
RUN apt-get update && apt-get install -y wget unzip curl gnupg \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/* \
    \
    # Install matching Chromedriver version
    && CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d. -f1) \
    && DRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION}") \
    && wget -q "https://chromedriver.storage.googleapis.com/${DRIVER_VERSION}/chromedriver_linux64.zip" \
    && unzip chromedriver_linux64.zip -d /usr/local/bin/ \
    && rm chromedriver_linux64.zip \
    && chmod +x /usr/local/bin/chromedriver

ENV PATH="/usr/local/bin:${PATH}"

# Copy compiled project
COPY --from=build /app /app

# Run tests
CMD ["mvn", "test"]

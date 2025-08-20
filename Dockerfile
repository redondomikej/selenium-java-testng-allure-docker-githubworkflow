# Use Maven with JDK 21 as base
FROM maven:3.9-eclipse-temurin-21-jammy
WORKDIR /app

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    xvfb \
    gnupg \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Add Google Chrome repository and install stable version
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Set environment for headless Chrome
ENV CI=true
ENV DISPLAY=:99

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies only
RUN mvn dependency:go-offline -B

# Default command: run tests
CMD ["mvn", "test"]

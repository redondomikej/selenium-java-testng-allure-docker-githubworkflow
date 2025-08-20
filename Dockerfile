FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -DskipTests

FROM maven:3.9.9-eclipse-temurin-21

# Install Chrome + ChromeDriver + dependencies
RUN apt-get update && apt-get install -y wget gnupg unzip curl \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
                          chromium-driver \
                          fonts-liberation \
                          libasound2 \
                          libatk-bridge2.0-0 \
                          libatk1.0-0 \
                          libcups2 \
                          libdbus-1-3 \
                          libgdk-pixbuf2.0-0 \
                          libnspr4 \
                          libnss3 \
                          libx11-xcb1 \
                          libxcomposite1 \
                          libxdamage1 \
                          libxrandr2 \
                          xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# Symlink chrome
RUN ln -s /usr/bin/google-chrome /usr/bin/chrome

WORKDIR /app
COPY --from=build /app /app

# Run tests
CMD ["mvn", "test"]

# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run with Maven and browsers
FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app

# Install Chrome and ChromeDriver
RUN apt-get update && apt-get install -y wget unzip gnupg2 curl \
    fonts-liberation libnss3 libgconf-2-4 libxss1 libasound2 libatk-bridge2.0-0 libgtk-3-0 \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' \
    && apt-get update && apt-get install -y google-chrome-stable \
    && CHROMEDRIVER_VERSION=$(curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE) \
    && wget -O /tmp/chromedriver.zip "https://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip" \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && chmod +x /usr/local/bin/chromedriver \
    && rm /tmp/chromedriver.zip \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Firefox and GeckoDriver
RUN apt-get update && apt-get install -y firefox \
    && GECKODRIVER_VERSION=$(curl -s https://api.github.com/repos/mozilla/geckodriver/releases/latest | grep '"tag_name":' | sed -E 's/.*"v([^"]+)".*/\1/') \
    && wget -O /tmp/geckodriver.tar.gz "https://github.com/mozilla/geckodriver/releases/download/v$GECKODRIVER_VERSION/geckodriver-v$GECKODRIVER_VERSION-linux64.tar.gz" \
    && tar -xvzf /tmp/geckodriver.tar.gz -C /usr/local/bin/ \
    && chmod +x /usr/local/bin/geckodriver \
    && rm /tmp/geckodriver.tar.gz \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Optional: set CI env variable for headless
ENV CI=true

# Copy compiled project
COPY --from=build /app /app

# Run tests
CMD ["mvn", "test"]
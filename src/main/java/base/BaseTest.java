package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    public void startBrowser(String browserType, boolean headless, String url) {
        try {
            String seleniumUrl = System.getenv("SELENIUM_URL");
            if (seleniumUrl == null || seleniumUrl.isEmpty()) {
                seleniumUrl = "http://localhost:4444/wd/hub"; // default for local
            }

            switch (browserType.toLowerCase()) {
                case "chrome" -> {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--window-size=1920,1080");

                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }

                    driver = new RemoteWebDriver(new URL(seleniumUrl), chromeOptions);
                }
                case "firefox" -> {
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    if (headless) {
                        firefoxOptions.addArguments("--headless");
                    }

                    driver = new RemoteWebDriver(new URL(seleniumUrl), firefoxOptions);
                }
                default -> throw new IllegalArgumentException("Browser type not supported");
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.get(url);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start browser: " + e.getMessage(), e);
        }
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

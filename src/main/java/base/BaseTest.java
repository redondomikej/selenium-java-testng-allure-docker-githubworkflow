package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseTest {
    protected WebDriver driver;

    private String getDriverPath(String browserType) {
        // Docker paths
        String chromePath = "/usr/local/bin/chromedriver";
        String geckoPath = "/usr/local/bin/geckodriver";

        // Local default paths (Windows example)
        String chromeLocal = "C:\\tools\\chromedriver.exe";
        String geckoLocal = "C:\\tools\\geckodriver.exe";

        boolean isDocker = Files.exists(Paths.get("/usr/local/bin"));

        return switch (browserType.toLowerCase()) {
            case "chrome" -> isDocker ? chromePath : chromeLocal;
            case "firefox" -> isDocker ? geckoPath : geckoLocal;
            default -> throw new IllegalArgumentException("browser type not supported");
        };
    }

    public void startBrowser(String browserType, boolean headless, String url) {
        String driverPath = getDriverPath(browserType);

        switch (browserType.toLowerCase()) {
            case "chrome" -> {
                System.setProperty("webdriver.chrome.driver", driverPath);

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--user-data-dir=/tmp/chrome-profile-" + System.currentTimeMillis());

                boolean isCI = System.getenv("GITHUB_ACTIONS") != null;
                if (headless || isCI) {
                    chromeOptions.addArguments("--headless=new");
                }

                chromeOptions.addArguments("--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
            }
            case "firefox" -> {
                System.setProperty("webdriver.gecko.driver", driverPath);

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }

                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                driver = new FirefoxDriver(firefoxOptions);
            }
            default -> throw new IllegalArgumentException("browser type not supported");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(url);
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

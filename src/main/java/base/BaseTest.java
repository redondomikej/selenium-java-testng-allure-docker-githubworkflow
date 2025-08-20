package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    public void startBrowser(String browserType, boolean headless, String url){
        switch (browserType.toLowerCase()){
            case "chrome" -> {
//                WebDriverManager.chromedriver().setup();
                System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
                ChromeOptions chromeOptions = new ChromeOptions();
                // Always add Docker-friendly flags
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--user-data-dir=/tmp/chrome-profile-" + System.currentTimeMillis());

                if(headless){
                    chromeOptions.addArguments("--headless=new");
                }
                driver = new ChromeDriver(chromeOptions);
                // Safer than maximize in Docker
                chromeOptions.addArguments("--window-size=1920,1080");
            }
            default -> throw new IllegalArgumentException("browser type not supported");
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(url);
    }

    public void closeBrowser(){
        if(driver != null){
            driver.quit();
            driver = null;
        }
    }

}

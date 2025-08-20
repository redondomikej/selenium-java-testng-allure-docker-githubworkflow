import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class PageTest extends BaseTest {

    @BeforeClass
    public void beforeClass(){
        startBrowser("chrome",true,"https://testautomationpractice.blogspot.com/");
    }

    @Test
    public void testFormGroup(){

        // 1️⃣ Text Fields
        WebElement name = driver.findElement(By.id("name"));
        WebElement email = driver.findElement(By.id("email"));
        WebElement phone = driver.findElement(By.id("phone"));

        Assert.assertTrue(name.isDisplayed(), "Name field should be displayed");
        name.sendKeys("Mike");
        Assert.assertEquals(name.getAttribute("value"), "Mike");

        Assert.assertTrue(email.isDisplayed(), "Email field should be displayed");
        email.sendKeys("mike@test.com");
        Assert.assertEquals(email.getAttribute("value"), "mike@test.com");

        Assert.assertTrue(phone.isDisplayed(), "Phone field should be displayed");
        phone.sendKeys("0912345678");
        Assert.assertEquals(phone.getAttribute("value"), "0912345678");

        // 2️⃣ Textarea
        WebElement address = driver.findElement(By.id("textarea"));
        address.sendKeys("Manila, Philippines");
        Assert.assertEquals(address.getAttribute("value"), "Manila, Philippines");

        // 3️⃣ Radio Buttons
        WebElement male = driver.findElement(By.id("male"));
        WebElement female = driver.findElement(By.id("female"));

        male.click();
        Assert.assertTrue(male.isSelected(), "Male should be selected");
        Assert.assertFalse(female.isSelected(), "Female should NOT be selected");

        // 4️⃣ Checkboxes
        WebElement monday = driver.findElement(By.id("monday"));
        WebElement friday = driver.findElement(By.id("friday"));
        monday.click();
        friday.click();

        Assert.assertTrue(monday.isSelected(), "Monday should be selected");
        Assert.assertTrue(friday.isSelected(), "Friday should be selected");

        // 5️⃣ Single Dropdown (Country)
        Select country = new Select(driver.findElement(By.id("country")));
        country.selectByVisibleText("Canada");
        Assert.assertEquals(country.getFirstSelectedOption().getText(), "Canada");

        // 6️⃣ Multi Dropdown (Colors)
        Select colors = new Select(driver.findElement(By.id("colors")));
        colors.selectByVisibleText("Red");
        colors.selectByVisibleText("Blue");

        List<WebElement> selectedColors = colors.getAllSelectedOptions();
        Assert.assertTrue(selectedColors.stream().anyMatch(o -> o.getText().equals("Red")));
        Assert.assertTrue(selectedColors.stream().anyMatch(o -> o.getText().equals("Blue")));

        // 7️⃣ Sorted List (Animals)
        Select animals = new Select(driver.findElement(By.id("animals")));
        animals.selectByVisibleText("Dog");
        Assert.assertEquals(animals.getFirstSelectedOption().getText(), "Dog");

    }
}

package com.olx.pages;
import com.olx.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
import java.time.Duration;

public class HomePage {
	
	private final WebDriver driver;
    private static final String URL = "https://www.olx.in/";
    
    public HomePage() {
        // Grabs the specific driver for the current parallel thread
        this.driver = WebDriverFactory.getDriver();
    }
    
    public void open() {
        driver.get(URL);
    }
    public String getTitle() {
        return driver.getTitle();
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<String> getClickableLinks() {
        return driver.findElements(By.tagName("a")).stream()
                .filter(link -> link.isDisplayed() && link.isEnabled())
                .map(link -> {
                    String text = link.getText().trim();
                    String href = link.getAttribute("href");
                    return text.isEmpty() ? href : text + " (" + href + ")";
                })
                .toList();
    }

    public void clickHeaderAccountIcon() {
        clickFirstAvailable(
                By.cssSelector("button[aria-label*='account' i], button[aria-label*='profile' i], button[aria-label*='login' i], a[aria-label*='account' i], a[aria-label*='profile' i], a[aria-label*='login' i]"),
                By.xpath("//*[self::button or self::a][contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]")
        );
    }

    public void clickContinueWithPhone() {
        clickFirstAvailable(By.xpath("//*[self::button or self::a][translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='continue with phone']"));
    }

    public void enterPhoneNumber(String phoneNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='tel'], input[name*='phone' i], input[placeholder*='phone' i], input[placeholder*='mobile' i]")));
        phoneInput.clear();
        phoneInput.sendKeys(phoneNumber);
    }

    public void clickNext() {
        clickFirstAvailable(By.xpath("//button[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='next']"),
                By.cssSelector("input[type='submit'][value='Next'], input[type='button'][value='Next']"));
    }

    private void clickFirstAvailable(By... locators) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (org.openqa.selenium.TimeoutException ignored) {
                // Try the next accessible locator used by OLX's responsive header/login dialog.
            }
        }
        throw new org.openqa.selenium.NoSuchElementException("Could not find a clickable element for any supplied locator");
    }

}

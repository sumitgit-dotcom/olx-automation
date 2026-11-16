package com.olx.pages;
import com.olx.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import java.util.List;

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

}

package com.olx.stepdefinitions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;

public class CategoryLocationSteps {
	
	  private WebDriver driver;
	    private WebDriverWait wait;
	    private Actions actions;
	    
	    @Before
	    public void setUp() {
	        WebDriverManager.chromedriver().setup();
	        ChromeOptions options = new ChromeOptions();
	        
	        // Only these arguments are needed for a visible browser
	        options.addArguments("--headless=new");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");
	        options.addArguments("--remote-allow-origins=*");
	        options.addArguments("--window-size=1920,1080");

	        driver = new ChromeDriver(options);
	        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        actions = new Actions(driver);
	    }
	    
	    @Given("I am on the homepage")
	    public void i_am_on_the_homepage() {
	        // Replace with your actual URL
	        driver.get("https://www.olx.in/"); 
	    }
	    
	    @When("I click on the category {string}")
	    public void i_click_on_the_category(String category) throws Exception {
	        try {
	            Thread.sleep(4000); // Give the page plenty of time to load popups

	            // 1. Try to close the initial Location popup if it exists
	            try {
	                WebElement closeLocationPopup = driver.findElement(By.xpath("//button[@class='_1Ny5V']"));
	                closeLocationPopup.click();
	                Thread.sleep(1000);
	            } catch (Exception e) {
	                // Popup didn't appear, continue
	            }

	            // 2. Find the category link
	            // Using a very broad XPath to find the specific text "Motorcycles"
	            String xpath = "//*[normalize-space(text())='" + category + "']";
	            WebElement categoryLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

	            // 3. Use JavaScript to click (bypasses 'element not clickable' errors)
	            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", categoryLink);
	            Thread.sleep(500);
	            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", categoryLink);
	            
	            System.out.println("Clicked on category: " + category);

	        } catch (Exception e) {
	            System.out.println("Failed to click category: " + e.getMessage());
	            throw e;
	        }
	    }
	    @And("I click on the subcategory {string} in the left pane")
	    public void i_click_on_the_subcategory_in_the_left_pane(String subcategory) throws InterruptedException {
	        Thread.sleep(3000); // Wait for the page to load after clicking category
	        String xpath = "//a[contains(., '" + subcategory + "')]";
	        WebElement subcategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
	        actions.moveToElement(subcategoryLink).click().perform();
	        System.out.println("Clicked on subcategory: " + subcategory);
	    }
	    
	    @And("I scroll and click on the location {string}")
	    public void i_scroll_and_click_on_the_location(String location) throws InterruptedException {
	        Thread.sleep(3000);
	        String xpath = "//*[contains(text(),'" + location + "')]";
	        WebElement locationOption = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	        actions.moveToElement(locationOption).perform();
	        Thread.sleep(1000); 
	        actions.click(locationOption).perform();
	        System.out.println("Clicked on location: " + location);
	    }
	    
	    @Then("I print the search results")
	    public void i_print_the_search_results() throws InterruptedException {
	        // Wait for the results page to load
	        Thread.sleep(3000);
	        
	        List<WebElement> results = driver.findElements(By.xpath("//li[@data-aut-id='itemBox']"));
	        
	        System.out.println("========== RESULTS FOUND ==========");
	        if (results.isEmpty()) {
	            System.out.println("No results found for this category and location.");
	        } else {
	            System.out.println("Total results found: " + results.size());
	            
	            
	            for (int i = 0; i < Math.min(5, results.size()); i++) {
	                // Assuming the title is inside an h4 tag. Update this locator if the site uses something else.
	                String title = results.get(i).findElement(By.xpath(".//h4")).getText();
	                System.out.println("Result " + (i + 1) + ": " + title);
	            }
	        }
	        System.out.println("===================================");
	    }
	    
	    
	    @After
	    public void tearDown() {
	        if (driver != null) {
	            driver.quit();
	        }
	    }

}

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
import org.apache.commons.io.FileUtils;


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
	            Thread.sleep(5000); // Wait 5 seconds for page to fully load

	            // 1. Try to click the "Location" popup if it blocks the screen
	            try {
	                WebElement locationPopup = driver.findElement(By.xpath("//button[contains(text(),'Confirm')] | //button[contains(text(),'Close')]"));
	                locationPopup.click();
	                System.out.println("Closed a popup.");
	                Thread.sleep(2000);
	            } catch (Exception e) {
	                // No popup, continue
	            }

	            // 2. Find the category
	            String xpath = "//span[contains(text(),'" + category + "')] | //a[contains(text(),'" + category + "')]";
	            WebElement categoryLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	            
	            // 3. Click via JavaScript
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", categoryLink);
	            System.out.println("Clicked on category: " + category);

	        } catch (Exception e) {
	            // If it fails, take a screenshot so we can see what Jenkins sees
	            System.out.println("FAILED TO CLICK CATEGORY. Taking screenshot...");
	            try {
	                org.openqa.selenium.TakesScreenshot ts = (org.openqa.selenium.TakesScreenshot) driver;
	                java.io.File source = ts.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
	                // Now that we imported FileUtils, we can just use it directly
	                FileUtils.copyFile(source, new java.io.File("target/failure_screenshot.png"));
	                System.out.println("Screenshot saved to target/failure_screenshot.png");
	            } catch (Exception ex) {
	                System.out.println("Could not save screenshot.");
	            }
	            throw e; // Fail the test
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

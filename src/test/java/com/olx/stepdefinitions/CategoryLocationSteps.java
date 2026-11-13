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

import java.io.IOException;
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
	        
	        // REMOVE or COMMENT OUT this line:
	        // options.addArguments("--headless=new");
	        
	        // Keep these:
	        options.addArguments("--disable-blink-features=AutomationControlled");
	        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");
	        options.addArguments("--window-size=1920,1080");

	        driver = new ChromeDriver(options);
	        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
	        
	        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        actions = new Actions(driver);
	    }
	    
	    @Given("I am on the homepage")
	    public void i_am_on_the_homepage() {
	        driver.get("https://www.olx.in/"); 
	        System.out.println("Navigated to OLX. Current URL: " + driver.getCurrentUrl());
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
	    public void i_click_on_the_subcategory_in_the_left_pane(String subcategory) throws InterruptedException, Exception {
	        // 1. Wait a full 5 seconds for the Motorcycles page to load completely
	        Thread.sleep(5000); 
	        
	        // 2. Find the element by text
	        String xpath = "//*[contains(text(), '" + subcategory + "')]";
	        
	        try {
	            WebElement subcategoryLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	            
	            // 3. Scroll to it and click using JavaScript (bypasses overlay/clickable issues)
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subcategoryLink);
	            Thread.sleep(1000);
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subcategoryLink);
	            
	            System.out.println("Clicked on subcategory: " + subcategory);
	            
	        } catch (Exception e) {
	            // Screenshot debug if it fails again
	            System.out.println("FAILED TO CLICK SUBCATEGORY. Taking screenshot...");
	            org.openqa.selenium.TakesScreenshot ts = (org.openqa.selenium.TakesScreenshot) driver;
	            java.io.File source = ts.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
	            FileUtils.copyFile(source, new java.io.File("target/subcategory_failure.png"));
	            throw e;
	        }
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
	        System.out.println("Waiting for results page to load...");
	        Thread.sleep(5000); 
	        
	        // Scroll down to trigger lazy loading
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        js.executeScript("window.scrollBy(0, 500);");
	        Thread.sleep(2000);

	        // Find ALL elements that act as a result card (OLX changes this class, so we look for the data attribute)
	        List<WebElement> results = driver.findElements(By.xpath("//li[contains(@class, '_3gXl_') or @data-aut-id='itemBox']"));
	        
	        System.out.println("========== RESULTS FOUND ==========");
	        if (results.isEmpty()) {
	            System.out.println("No results found. Printing Page Source to debug:");
	            // If it still fails, we print the page title to see where we ended up
	            System.out.println("Current URL: " + driver.getCurrentUrl());
	            System.out.println("Page Title: " + driver.getTitle());
	        } else {
	            System.out.println("Total results found: " + results.size());
	            for (int i = 0; i < Math.min(5, results.size()); i++) {
	                // Get the text of the result card and print the first line (the title)
	                String title = results.get(i).getText().split("\n")[0];
	                System.out.println("Result " + (i + 1) + ": " + title);
	            }
	        }
	        System.out.println("===================================");
	    }
	    
	    
	    @After
	    public void tearDown() {
	        if (driver != null) {
	            // Wait 10 seconds before closing so you can see the final page
	            try {
	                System.out.println("Test finished. Keeping browser open for 10 seconds so you can see it...");
	                Thread.sleep(10000); 
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            driver.quit();
	        }

}}

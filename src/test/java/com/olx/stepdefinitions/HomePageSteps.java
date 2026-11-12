package com.olx.stepdefinitions;

import com.olx.pages.HomePage;
import com.olx.utils.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class HomePageSteps {
	
	 private HomePage homePage;
	 
	 @Given("I launch the browser and navigate to OLX home page")
	    public void i_launch_browser_and_navigate_to_olx_home_page() {
	        // Initialize the page object (which grabs the ThreadLocal driver)
	        homePage = new HomePage();
	        
	        // Navigate to the OLX URL
	        homePage.open();
	        
	        // Print the URL to the console
	        System.out.println("✅ Navigated to: " + homePage.getCurrentUrl());
	    }

	    @Then("I should see the page title printed in the console")
	    public void i_should_see_the_page_title_printed() {
	        // Get the title and print it nicely formatted
	        String title = homePage.getTitle();
	        System.out.println("=========================================");
	        System.out.println("🌐 Page Title  : " + title);
	        System.out.println("🔗 Current URL : " + homePage.getCurrentUrl());
	        System.out.println("=========================================");
	    }

	    @Then("the page title should contain {string}")
	    public void the_page_title_should_contain(String expected) {
	        // Validate the title contains the expected string
	        String title = homePage.getTitle();
	        System.out.println("Validating title contains: " + expected);
	        
	        Assert.assertTrue(title.contains(expected),
	                "Expected title to contain '" + expected + "' but was: " + title);
	    }

	    @After
	    public void tearDown() {
	        // This runs after EACH scenario, safely closing the specific browser thread
	        WebDriverFactory.quitDriver();
	    }

}

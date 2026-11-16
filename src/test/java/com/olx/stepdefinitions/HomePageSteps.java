package com.olx.stepdefinitions;

import com.olx.pages.HomePage;
import com.olx.utils.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import java.util.List;

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

        @Then("I should see the clickable home page links printed in the console")
        public void print_clickable_home_page_links() {
            List<String> links = homePage.getClickableLinks();
            System.out.println("=========================================");
            System.out.println("Clickable links on OLX home page: " + links.size());
            links.forEach(link -> System.out.println(" - " + link));
            System.out.println("=========================================");
            Assert.assertFalse(links.isEmpty(), "No clickable links were found on the OLX home page");
        }

	    @After
	    public void tearDown() {
	        // This runs after EACH scenario, safely closing the specific browser thread
	        WebDriverFactory.quitDriver();
	    }

}

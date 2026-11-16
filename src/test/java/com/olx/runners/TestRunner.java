package com.olx.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

@CucumberOptions(
	    features = "src/test/resources/features",
	    glue = "com.olx.stepdefinitions", // Make sure this matches your steps folder
	    tags = "@printable-links",
	    plugin = {"pretty"}
	)

public class TestRunner extends AbstractTestNGCucumberTests {
	
	
	 
    }



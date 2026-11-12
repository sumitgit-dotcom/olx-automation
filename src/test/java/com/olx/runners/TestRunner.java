package com.olx.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.olx.stepdefinitions", 
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)

public class TestRunner extends AbstractTestNGCucumberTests {
	
	
	 @BeforeClass(alwaysRun = true)
	    @Parameters({"browser"})
	    public void setBrowser(@Optional("chrome") String browser) { // Add @Optional
	        System.setProperty("browser", browser);
	        System.out.println(">>> Running on browser = " + browser);
	    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}

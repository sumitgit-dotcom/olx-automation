Feature: OLX Home Page

  Scenario: Open OLX home page and verify title
    Given I launch the browser and navigate to OLX home page
    Then I should see the page title printed in the console
    And the page title should contain "OLX"
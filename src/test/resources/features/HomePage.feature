Feature: OLX Home Page

  Scenario: Open OLX home page and verify title
    Given I launch the browser and navigate to OLX home page
    Then I should see the page title printed in the console
    And the page title should contain "OLX"

  @printable-links
  Scenario: Count and print clickable links on the home page
    Given I launch the browser and navigate to OLX home page
    Then I should see the clickable home page links printed in the console

  @login
  Scenario: Start login using a phone number
    Given I launch the browser and navigate to OLX home page
    Then I open the login dialog from the header
    And I choose to continue with phone
    And I enter phone number "8871220199"
    And I click Next

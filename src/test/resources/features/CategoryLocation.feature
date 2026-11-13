Feature: Category and Location Search

  @second
  Scenario: Search for Scooters in Madhya Pradesh
    Given I am on the homepage
    When I click on the category "Motorcycles"
    And I click on the subcategory "Scooters" in the left pane
    And I scroll and click on the location "Madhya Pradesh"
    Then I print the search results
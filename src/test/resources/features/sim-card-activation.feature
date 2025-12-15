Feature: SIM Card Activation
  As a telecom service provider
  I want to activate SIM cards
  So that customers can use mobile services

  Scenario: Successful SIM card activation
    Given I have a valid SIM card with ICCID "1255789453849037777"
    And I have customer email "success@test.com"
    When I submit an activation request
    Then the activation should be successful
    And the database should show the SIM card as active

  Scenario: Failed SIM card activation  
    Given I have an invalid SIM card with ICCID "8944500102198304826"
    And I have customer email "failure@test.com"
    When I submit an activation request
    Then the activation should fail
    And the database should show the SIM card as inactive

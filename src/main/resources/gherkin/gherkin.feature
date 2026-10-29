Feature: User Login
  Scenario: Successful login returns a JWT
    Given a registered user with registered email and password
    When I send a POST request to "/api/v1/login" with the correct email and password
    Then the response status should be status 200 - OK
    And the response should contain a token to be used until logout or until it expires
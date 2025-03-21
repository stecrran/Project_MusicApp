Feature: Get All Songs

  Background:
    * def token = callonce read('login.feature') { username: 'Admin', password: 'Admin' }

  Scenario: Fetch all songs
    Given url 'http://localhost:9091/api/music'
    When method GET
    Then status 200
    And match response == []  # Validates response is an array



    
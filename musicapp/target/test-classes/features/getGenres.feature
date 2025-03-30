Feature: Get Genre Count

	Background:
	  * url 'http://localhost:9091/api'
	  * def loginResponse = callonce read('login.feature') { username: 'Admin', password: 'Admin' }
	  * def token = loginResponse.jwt
	  * header Authorization = 'Bearer ' + token
	  * header Accept = 'application/json'

  Scenario: Fetch song genre count
    Given url 'http://localhost:9091/api/music/genre-count'
    When method GET
    Then status 200
    And match response == '#present'  

  Scenario: Fetch unique user count per genre
    Given url 'http://localhost:9091/api/music/genre-users'
    When method GET
    Then status 200
    And match response == '#present'  
    
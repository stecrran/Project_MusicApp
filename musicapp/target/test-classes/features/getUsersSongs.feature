Feature: Get Users Songs  

  Background:
    * url baseUrl
    * def loginResponse = callonce read('login.feature') { username: 'Admin', password: 'Admin' }
    * def token = loginResponse.jwt


  Scenario: Fetch logged-in user's songs
    Given url baseUrl + '/music/my-songs'
    And header Authorization = 'Bearer ' + token
    When method GET
    Then status 200
    And match response == []  

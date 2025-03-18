Feature: User Login

Scenario: Authenticate and get JWT token
  # The parameters 'baseUrl', 'username' and 'password' are passed in from karate-config.js
  Given url baseUrl + '/auth/login'
  And request { username: "Admin", password: "Admin" }
  When method post
  Then status 200
  * def jwt = response.jwt

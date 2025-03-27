Feature: SPOTIFY_USER should not be able to access admin features

Background:
  * def baseUrl = 'http://localhost:9091/api'
  * def randomUsername = 'spotifyuser-karate-' + java.time.Instant.now().toEpochMilli()
  * def password = 'Password345'

  # Create SPOTIFY_USER using admin
  Given url baseUrl + '/auth/login'
  And request { username: 'Admin', password: 'Admin' }
  When method POST
  Then status 200
  * def adminToken = response.jwt

  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + adminToken
  And request { username: #(randomUsername), password: #(password), roles: ['SPOTIFY_USER'] }
  When method POST
  Then status 200


Scenario: SPOTIFY_USER should not be able to create an ADMIN user

  # Attempt to register a new user with ADMIN role using SPOTIFY_USER token
  Given url baseUrl + '/auth/login'
  And request { username: #(randomUsername), password: #(password) }
  When method POST
  Then status 200
  * def userToken = response.jwt
  
  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + userToken
  And request { username: 'illegal-admin-attempt', password: 'Password789', roles: ['ADMIN'] }
  When method POST
  Then status 403

Scenario: SPOTIFY_USER should not be able to create a SPOTIFY_USER user
  # Login as the existing SPOTIFY_USER
  Given url baseUrl + '/auth/login'
  And request { username: #(randomUsername), password: #(password) }
  When method POST
  Then status 200
  * def userToken = response.jwt

  # Attempt to register a new SPOTIFY_USER using this token
  * def anotherUsername = 'spotifyuser-karate-denied-' + java.time.Instant.now().toEpochMilli()
  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + userToken
  And request { username: #(anotherUsername), password: 'Password123', roles: ['SPOTIFY_USER'] }
  When method POST
  Then status 403
  
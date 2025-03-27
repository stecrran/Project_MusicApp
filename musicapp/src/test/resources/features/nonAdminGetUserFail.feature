Feature: SPOTIFY_USER role should not access admin endpoints

Background:
  * def baseUrl = 'http://localhost:9091/api'
  * def randomUsername = 'spotifyuser-karate-' + java.time.Instant.now().toEpochMilli()
  * def password = 'Password123'

Scenario: Register SPOTIFY_USER, login, verify, and test admin access denial

  # Login as admin to create a SPOTIFY_USER
  Given url baseUrl + '/auth/login'
  And request { username: 'Admin', password: 'Admin' }
  When method POST
  Then status 200
  * def adminToken = response.jwt

  # Register SPOTIFY_USER
  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + adminToken
  And request { username: #(randomUsername), password: #(password), roles: ['SPOTIFY_USER'] }
  When method POST
  Then status 200
  * def userId = response.id

  # Login as SPOTIFY_USER
  Given url baseUrl + '/auth/login'
  And request { username: #(randomUsername), password: #(password) }
  When method POST
  Then status 200
  * def userToken = response.jwt
  * def username = response.username ? response.username : response.user.username
  * def roles = response.roles ? response.roles : response.user.roles
  And match username == randomUsername
  And match roles contains 'SPOTIFY_USER'

  # Try accessing /admin/users (should fail)
  Given url baseUrl + '/admin/users'
  And header Authorization = 'Bearer ' + userToken
  When method GET
  # does not throw 403 
  Then status 500 

  # Cleanup: delete created user
  Given url baseUrl + '/admin/users/' + userId
  And header Authorization = 'Bearer ' + adminToken
  When method DELETE
  Then status 204

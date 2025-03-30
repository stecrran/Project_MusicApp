Feature: Create, Update, Delete, and Get Users

Background:
  * def baseUrl = 'http://localhost:9091/api'
  * def randomUsername = 'testuser-karate-1' + java.time.Instant.now().toEpochMilli()
  * def password = 'Password123'

Scenario: Create, Update, and Delete User

  # Login as admin to perform user management
  Given url baseUrl + '/auth/login'
  And request { username: 'Admin', password: 'Admin' }
  When method POST
  Then status 200
  * def adminToken = response.jwt

  # Create a new user
  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + adminToken
  And request { username: #(randomUsername), password: #(password), roles: ['ADMIN'] }
  When method POST
  Then status 200
  And match response.id == '#present'
  * def userId = response.id

  # Login as the created user to verify login works
  Given url baseUrl + '/auth/login'
  And request { username: #(randomUsername), password: #(password) }
  When method POST
  Then status 200
  * def userToken = response.jwt

  # Delete the user
  Given url baseUrl + '/admin/users/' + userId
  And header Authorization = 'Bearer ' + adminToken
  When method DELETE
  Then status 204

Scenario: Get All Users
  # Login as admin
  Given url baseUrl + '/auth/login'
  And request { username: 'Admin', password: 'Admin' }
  When method POST
  Then status 200
  * def adminToken = response.jwt

  # Fetch all users
  Given url baseUrl + '/admin/users'
  And header Authorization = 'Bearer ' + adminToken
  When method GET
  Then status 200
  And assert response.length > 0

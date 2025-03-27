Feature: Create and verify user with SPOTIFY_USER role

Background:
  * def baseUrl = 'http://localhost:9091/api'
  * def randomUsername = 'spotifyuser-karate-' + java.time.Instant.now().toEpochMilli()
  * def password = 'Password123'

Scenario: Register, login, and verify user with SPOTIFY_USER role

  # Login as admin to create a new user
  Given url baseUrl + '/auth/login'
  And request { username: 'Admin', password: 'Admin' }
  When method POST
  Then status 200
  * def adminToken = response.jwt

  # Register a new user with SPOTIFY_USER role
  Given url baseUrl + '/admin/register'
  And header Authorization = 'Bearer ' + adminToken
  And request { username: #(randomUsername), password: #(password), roles: ['SPOTIFY_USER'] }
  When method POST
  Then status 200
  And match response.id == '#present'
  * def userId = response.id

	# Login as the new SPOTIFY_USER
	Given url baseUrl + '/auth/login'
	And request { username: #(randomUsername), password: #(password) }
	When method POST
	Then status 200
	* def userToken = response.jwt
	* print 'Login response:', response
	* def username = response.username ? response.username : response.user.username
	* def roles = response.roles ? response.roles : response.user.roles
	And match username == randomUsername
	And match roles contains 'SPOTIFY_USER'

	# Fetch all users and find the one we just created
	Given url baseUrl + '/admin/users'
	And header Authorization = 'Bearer ' + adminToken
	When method GET
	Then status 200

	# Extract userId by filtering on username
	* def createdUser = response.find(x => x.username == randomUsername)
	* match createdUser != null
	* def userId = createdUser.id

  # Cleanup: Delete user
  Given url baseUrl + '/admin/users/' + userId
  And header Authorization = 'Bearer ' + adminToken
  When method DELETE
  Then status 204

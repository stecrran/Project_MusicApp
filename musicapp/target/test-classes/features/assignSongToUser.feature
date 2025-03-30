Feature: Assign Song to User

Background:
  * url 'http://localhost:9091/api'
  * def loginResponse = callonce read('login.feature') { username: 'Admin', password: 'Admin' }
  * def token = loginResponse.jwt
  * header Authorization = 'Bearer ' + token

  
  # Run `saveSong.feature` and capture response
	* def saveSongResponse = call read('saveAndRemoveSong.feature')
	* def songId = saveSongResponse.songId
	* karate.log('[DEBUG] Retrieved song ID:', songId)


Scenario: Admin assigns a song to a user
  * karate.log('[DEBUG] Assigning song to user with ID:', songId)
  Given path 'music', songId, 'assign', 'user1'  
  And header Authorization = 'Bearer ' + token
  When method POST
  Then status 200
  And match response == 'User assigned to song successfully.'

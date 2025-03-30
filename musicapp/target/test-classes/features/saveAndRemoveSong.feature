Feature: Save and Remove Song

Background:
  * url 'http://localhost:9091/api'
  * def loginResponse = callonce read('login.feature') { username: 'Admin', password: 'Admin' }
  * def token = loginResponse.jwt
  * header Authorization = 'Bearer ' + token
  * header Accept = 'application/json'

  # Generate random song details
  * def uuid = function() { return java.util.UUID.randomUUID().toString() }
  * def randomSpotifyId = uuid()
  * def randomTitle = 'Song-' + uuid().substring(0, 5)
  * def randomArtist = 'Artist-' + uuid().substring(0, 5)
  * def randomAlbum = 'Album-' + uuid().substring(0, 5)
  * def randomGenre = 'Genre-' + uuid().substring(0, 5)
  * def randomDuration = (Math.floor(Math.random() * (300000 - 60000)) + 60000)
  * def randomSpotifyUrl = 'https://open.spotify.com/track/' + randomSpotifyId


Scenario: Save a new song
  Given path 'music/save'
  And request
  """
  {
    "spotifyId": #(randomSpotifyId),
    "title": #(randomTitle),
    "artist": #(randomArtist),
    "album": #(randomAlbum),
    "genre": #(randomGenre),
    "durationMs": #(randomDuration),
    "spotifyUrl": #(randomSpotifyUrl)
  }
  """
  When method POST
  Then status 201
  * match response contains { id: '#number' }
	* def songId = response.id
	* def path = java.lang.System.getProperty('user.dir') + '/target/song-id.txt'
	* def Files = Java.type('java.nio.file.Files')
	* def Paths = Java.type('java.nio.file.Paths')
	* eval Files.writeString(Paths.get(path), songId + '')


Scenario: Remove the saved song from user collection
	* def path = java.lang.System.getProperty('user.dir') + '/target/song-id.txt'
	* def Files = Java.type('java.nio.file.Files')
	* def Paths = Java.type('java.nio.file.Paths')
	* def songId = Files.readString(Paths.get(path))

  Given path 'music/remove/' + songId
  When method DELETE
  Then status 200
  And match response == 'Song removed from your collection.'


Scenario: Admin deletes the song
  * def path = 'target/song-id.txt'
  * def songId = Java.type('java.nio.file.Files').readString(Java.type('java.nio.file.Paths').get(path))

  Given path 'music/' + songId
  When method DELETE
  Then status 200
  And match response == 'Song deleted successfully.'

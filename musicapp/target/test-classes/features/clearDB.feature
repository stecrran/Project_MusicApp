Feature: Clear only songs in musicappdb before tests

Background:
  * url 'http://localhost:9091/api'
  * def loginResponse = callonce read('login.feature') { username: 'Admin', password: 'Admin' }
  * def token = loginResponse.jwt
  * header Authorization = 'Bearer ' + token

Scenario: Clear all songs before tests
  * print '[DEBUG] Sending request to clear all songs...'
  Given path 'test', 'clear-songs'
  When method DELETE
  Then status 200
  * print '✅ All songs cleared before test'

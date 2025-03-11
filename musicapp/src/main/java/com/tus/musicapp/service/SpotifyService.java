package com.tus.musicapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class SpotifyService {

    private static final String SPOTIFY_TRACK_URL = "https://api.spotify.com/v1/tracks/";
    private static final String SPOTIFY_ARTIST_URL = "https://api.spotify.com/v1/artists/";

    private static final Logger LOGGER = Logger.getLogger(SpotifyService.class.getName());

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Song fetchAndSaveSong(String spotifyTrackId, String accessToken) {
        try {
            // ✅ Step 1: Check if the song already exists
            if (songRepository.existsBySpotifyId(spotifyTrackId)) {
                System.out.println("🎵 Song already exists in DB. Skipping save.");
                return songRepository.findBySpotifyId(spotifyTrackId).orElse(null);
            }

            // ✅ Step 2: Fetch Track Details from Spotify API
            String trackUrl = SPOTIFY_TRACK_URL + spotifyTrackId;
            String trackResponse = fetchFromSpotify(trackUrl, accessToken);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode trackJson = mapper.readTree(trackResponse);

            String trackName = trackJson.get("name").asText();
            String albumName = trackJson.get("album").get("name").asText();
            String artistId = trackJson.get("artists").get(0).get("id").asText();
            String artistName = trackJson.get("artists").get(0).get("name").asText();

            // ✅ Step 3: Fetch Artist Genre
            String artistUrl = SPOTIFY_ARTIST_URL + artistId;
            String artistResponse = fetchFromSpotify(artistUrl, accessToken);
            JsonNode artistJson = mapper.readTree(artistResponse);
            List<String> genres = artistJson.has("genres") ? 
                                  mapper.convertValue(artistJson.get("genres"), List.class) :
                                  Collections.emptyList();

            String genre = genres.isEmpty() ? "Unknown" : String.join(", ", genres);

            // ✅ Step 4: Save to Database
            Song song = new Song(trackName, albumName, artistName, genre, spotifyTrackId);
            return songRepository.save(song);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String fetchFromSpotify(String url, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                LOGGER.warning("⚠️ Failed to fetch data from Spotify. Status Code: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe("❌ Error calling Spotify API: " + e.getMessage());
            return null;
        }
    }
    
}

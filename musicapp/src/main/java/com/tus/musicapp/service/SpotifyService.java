package com.tus.musicapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;

@Service
public class SpotifyService {

    private static final String SPOTIFY_TRACK_URL = "https://api.spotify.com/v1/tracks/";
    private static final String SPOTIFY_ARTIST_URL = "https://api.spotify.com/v1/artists/";

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Song fetchAndSaveSong(String spotifyTrackId, String accessToken) {
        try {
            // ✅ Fetch Track Details
            String trackUrl = SPOTIFY_TRACK_URL + spotifyTrackId;
            String trackResponse = fetchFromSpotify(trackUrl, accessToken);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode trackJson = mapper.readTree(trackResponse);

            String trackName = trackJson.get("name").asText();
            String albumName = trackJson.get("album").get("name").asText();
            String artistId = trackJson.get("artists").get(0).get("id").asText();
            String artistName = trackJson.get("artists").get(0).get("name").asText();
            int durationMs = trackJson.has("duration_ms") ? trackJson.get("duration_ms").asInt() : 0;
            String spotifyUrl = trackJson.get("external_urls").get("spotify").asText();

            // ✅ Fetch Artist Genre
            String artistUrl = SPOTIFY_ARTIST_URL + artistId;
            String artistResponse = fetchFromSpotify(artistUrl, accessToken);
            JsonNode artistJson = mapper.readTree(artistResponse);
            List<String> genres = artistJson.has("genres") ? 
                                  mapper.convertValue(artistJson.get("genres"), List.class) :
                                  Collections.emptyList();

            String genre = genres.isEmpty() ? "Unknown" : String.join(", ", genres);

            // Save to Database with seven parameters
            Song song = new Song(spotifyTrackId, trackName, artistName, albumName, genre, durationMs, spotifyUrl);
            return songRepository.save(song);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String fetchFromSpotify(String url, String accessToken) {
        return restTemplate.getForObject(url + "?access_token=" + accessToken, String.class);
    }
}

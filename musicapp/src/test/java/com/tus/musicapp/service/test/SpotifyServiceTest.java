package com.tus.musicapp.service.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.service.SpotifyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SpotifyService spotifyService;

    private static final String ACCESS_TOKEN = "testAccessToken";
    private static final String SPOTIFY_TRACK_ID = "testTrackId";
    private static final String TRACK_URL = "https://api.spotify.com/v1/tracks/" + SPOTIFY_TRACK_ID;
    private static final String ARTIST_URL = "https://api.spotify.com/v1/artists/testArtistId";

    private static final String TRACK_RESPONSE = "{" +
            "\"name\": \"Test Song\"," +
            "\"album\": {\"name\": \"Test Album\"}," +
            "\"artists\": [{\"id\": \"testArtistId\", \"name\": \"Test Artist\"}]," +
            "\"duration_ms\": 200000," +
            "\"external_urls\": {\"spotify\": \"https://spotify.com/testSong\"}" +
            "}";

    private static final String ARTIST_RESPONSE = "{" +
            "\"genres\": [\"Rock\", \"Pop\"]" +
            "}";

    @BeforeEach
    void setUp() {
        lenient().when(restTemplate.getForObject(eq(TRACK_URL + "?access_token=" + ACCESS_TOKEN), eq(String.class)))
                .thenReturn(TRACK_RESPONSE);

        lenient().when(restTemplate.getForObject(eq(ARTIST_URL + "?access_token=" + ACCESS_TOKEN), eq(String.class)))
                .thenReturn(ARTIST_RESPONSE);
    }

    @Test
    void fetchAndSaveSong_ShouldReturnSavedSong() {
        Song expectedSong = new Song(SPOTIFY_TRACK_ID, "Test Song", "Test Artist", "Test Album", "Rock, Pop", 200000, "https://spotify.com/testSong");
        when(songRepository.save(any(Song.class))).thenReturn(expectedSong);

        Song result = spotifyService.fetchAndSaveSong(SPOTIFY_TRACK_ID, ACCESS_TOKEN);

        assertNotNull(result);
        assertEquals("Test Song", result.getTitle());
        assertEquals("Test Album", result.getAlbum());
        assertEquals("Test Artist", result.getArtist());
        assertEquals("Rock, Pop", result.getGenre());
        assertEquals(200000, result.getDurationMs());
        assertEquals("https://spotify.com/testSong", result.getSpotifyUrl());
        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    void fetchAndSaveSong_ShouldHandleExceptionAndReturnNull() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("Spotify API error"));

        Song result = spotifyService.fetchAndSaveSong(SPOTIFY_TRACK_ID, ACCESS_TOKEN);

        assertNull(result);
    }
}

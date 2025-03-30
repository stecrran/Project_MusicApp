package com.tus.musicapp.model.test;

import org.junit.jupiter.api.Test;

import com.tus.musicapp.model.Music;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MusicTest {

    @Test
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        String spotifyId = "abc123";
        String title = "Test Song";
        String artist = "Test Artist";
        String album = "Test Album";
        String genre = "Pop";
        int durationMs = 200000;
        String spotifyUrl = "https://open.spotify.com/track/abc123";

        // Act
        Music music = new Music(spotifyId, title, artist, album, genre, durationMs, spotifyUrl);

        // Assert
        assertEquals(spotifyId, music.getSpotifyId());
        assertEquals(title, music.getTitle());
        assertEquals(artist, music.getArtist());
        assertEquals(album, music.getAlbum());
        assertEquals(genre, music.getGenre());
        assertEquals(durationMs, music.getDurationMs());
        assertEquals(spotifyUrl, music.getSpotifyUrl());

        music.setUsers(new HashSet<>());
        assertNotNull(music.getUsers());
    }

    @Test
    void defaultConstructor_ShouldAllowFieldSetting() {
        Music music = new Music();
        music.setId(1L);
        music.setSpotifyId("id123");
        music.setTitle("Title");
        music.setArtist("Artist");
        music.setAlbum("Album");
        music.setGenre("Rock");
        music.setDurationMs(150000);
        music.setSpotifyUrl("https://url");
        
        assertEquals(1L, music.getId());
        assertEquals("id123", music.getSpotifyId());
        assertEquals("Title", music.getTitle());
        assertEquals("Artist", music.getArtist());
        assertEquals("Album", music.getAlbum());
        assertEquals("Rock", music.getGenre());
        assertEquals(150000, music.getDurationMs());
        assertEquals("https://url", music.getSpotifyUrl());
    }
}


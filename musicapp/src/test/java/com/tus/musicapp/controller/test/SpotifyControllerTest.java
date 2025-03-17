package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.SpotifyController;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotifyControllerTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SpotifyController spotifyController;

    private Song song;

    @BeforeEach
    void setUp() {
        song = new Song();
        song.setId(1L);
        song.setTitle("Test Song");
    }

    @Test
    void getAllSongs_ShouldReturnSongList() {
        when(songRepository.findAll()).thenReturn(List.of(song));

        List<Song> response = spotifyController.getAllSongs();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("Test Song", response.get(0).getTitle());
        verify(songRepository, times(1)).findAll();
    }
}

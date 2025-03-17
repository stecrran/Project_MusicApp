package com.tus.musicapp.service.test;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.SongService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SongService songService;

    private User user;
    private Song song;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setSongCollection(new HashSet<>());

        song = new Song();
        song.setId(1L);
        song.setTitle("Test Song");
    }

    @Test
    void findAll_ShouldReturnSongList() {
        when(songRepository.findAll()).thenReturn(List.of(song));

        List<Song> response = songService.findAll();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("Test Song", response.get(0).getTitle());
        verify(songRepository, times(1)).findAll();
    }

    @Test
    void existsBySpotifyId_ShouldReturnTrue_WhenSongExists() {
        when(songRepository.existsBySpotifyId("123"))
                .thenReturn(true);

        assertTrue(songService.existsBySpotifyId("123"));
    }

    @Test
    void saveSong_ShouldSaveAndReturnSong() {
        when(songRepository.save(song)).thenReturn(song);

        Song response = songService.saveSong(song);

        assertNotNull(response);
        assertEquals("Test Song", response.getTitle());
        verify(songRepository, times(1)).save(song);
    }

    @Test
    void deleteById_ShouldDeleteSong() {
        doNothing().when(songRepository).deleteById(1L);

        songService.deleteById(1L);

        verify(songRepository, times(1)).deleteById(1L);
    }

    @Test
    void getGenreCount_ShouldReturnCorrectGenreCount() {
        song.setGenre("Rock");
        when(songRepository.findAll()).thenReturn(List.of(song));

        Map<String, Long> response = songService.getGenreCount();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.containsKey("Rock"));
        assertEquals(1, response.get("Rock"));
    }

    @Test
    void getUserSongs_ShouldReturnUserSongCollection() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        user.getSongCollection().add(song);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Set<Song> response = songService.getUserSongs();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertTrue(response.contains(song));
    }

    @Test
    void addSongToUser_ShouldAssociateSongWithUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        songService.addSongToUser(1L);

        assertTrue(user.getSongCollection().contains(song));
        verify(userRepository, times(1)).save(user);
    }
}

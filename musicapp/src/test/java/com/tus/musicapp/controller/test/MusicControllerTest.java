package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.MusicController;
import com.tus.musicapp.repos.*;
import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.mapper.SongMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicControllerTest {

    @Mock
    private SongService songService;

    @Mock
    private SongMapper songMapper;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private MusicController musicController;

    private Song song;
    private SongDto songDto;
    private MusicCreationDto musicCreationDto;
    
    @BeforeEach
    void setUp() {
        song = new Song();
        song.setId(1L);
        song.setTitle("Test Song");
        song.setUsers(new HashSet<>()); // Ensure users Set is initialized

        songDto = new SongDto();
        songDto.setId(1L);
        songDto.setTitle("Test Song");
        songDto.setUsers(List.of()); // Ensure users list is initialized
        
        musicCreationDto = new MusicCreationDto();
        musicCreationDto.setSpotifyId("123");
        musicCreationDto.setTitle("New Song");
        musicCreationDto.setArtist("Test Artist");
        musicCreationDto.setAlbum("Test Album");
        musicCreationDto.setGenre("Rock");
        musicCreationDto.setDurationMs(200000);
        musicCreationDto.setSpotifyUrl("https://spotify.com/test");
    }
    

    @Test
    void getAllSongs_ShouldReturnSongList() {
        when(songService.findAll()).thenReturn(List.of(song));
        when(songMapper.toDto(song)).thenReturn(songDto);

        ResponseEntity<List<SongDto>> response = musicController.getAllSongs();

        assertNotNull(response);
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Song", response.getBody().get(0).getTitle());
        verify(songService, times(1)).findAll();
    }

    @Test
    void saveSong_ShouldReturnCreated_WhenSongDoesNotExist() {
        // Mock authentication context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Mock userRepository
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Mock Song Service & Mapper
        when(songService.existsBySpotifyId(musicCreationDto.getSpotifyId())).thenReturn(false);
        when(songMapper.toEntity(musicCreationDto)).thenReturn(song);
        when(songService.saveSong(song)).thenReturn(song);
        when(songMapper.toDto(song)).thenReturn(songDto);

        ResponseEntity<?> response = musicController.saveSong(musicCreationDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(songService, times(1)).saveSong(song);
    }


    @Test
    void saveSong_ShouldReturnConflict_WhenSongExists() {
        when(songService.existsBySpotifyId(musicCreationDto.getSpotifyId())).thenReturn(true);

        ResponseEntity<?> response = musicController.saveSong(musicCreationDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void deleteSong_ShouldReturnOk_WhenSongExists() {
        when(songService.findById(1L)).thenReturn(Optional.of(song));
        doNothing().when(songService).deleteById(1L);

        ResponseEntity<?> response = musicController.deleteSong(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(songService, times(1)).deleteById(1L);
    }

    @Test
    void deleteSong_ShouldReturnNotFound_WhenSongDoesNotExist() {
        when(songService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = musicController.deleteSong(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteSong_ShouldReturnInternalServerError_OnException() {
        when(songService.findById(1L)).thenReturn(Optional.of(song));
        doThrow(new RuntimeException("Database error"))
                .when(songService).deleteById(1L);

        ResponseEntity<?> response = musicController.deleteSong(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(songService, times(1)).deleteById(1L);
    }
    
    @Test
    void getUserSongs_ShouldReturnUserSongList() {
        User user = new User();
        user.setUsername("testuser");
        user.setSongCollection(Set.of(song));
        song.setUsers(Set.of(user));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(songMapper.toDto(any(Song.class))).thenReturn(songDto); // Ensure mapping is mocked
        
        ResponseEntity<List<SongDto>> response = musicController.getUserSongs();

        assertNotNull(response);
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }


    @Test
    void assignSongToUser_ShouldReturnConflict_WhenUserAlreadyHasSong() {
        User user = new User();
        user.setUsername("testuser");
        user.setSongCollection(Set.of(song));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        ResponseEntity<?> response = musicController.assignSongToUser(1L, "testuser");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void removeSongFromUser_ShouldReturnNotFound_WhenUserDoesNotHaveSong() {
        User user = new User();
        user.setUsername("testuser");
        user.setSongCollection(Set.of());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        ResponseEntity<?> response = musicController.removeSongFromUser(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getGenreUserCount_ShouldReturnCorrectUserCountForGenres() {
        song.setGenre("Rock");
        User user = new User();
        user.setUsername("testuser");
        song.setUsers(Set.of(user));
        when(songRepository.findAll()).thenReturn(List.of(song));

        ResponseEntity<Map<String, Integer>> response = musicController.getGenreUserCount();

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().containsKey("Rock"));
        assertEquals(1, response.getBody().get("Rock"));
    }
}

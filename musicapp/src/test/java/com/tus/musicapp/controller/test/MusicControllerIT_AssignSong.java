package com.tus.musicapp.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.SongService;
import com.tus.musicapp.mapper.SongMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class MusicControllerIT_AssignSong {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SongRepository songRepository;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private SongService songService;

    @MockBean(name = "songMapper")
    private SongMapper songMapper;

    private User testUser;
    private Song testSong;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(Role.SPOTIFY_USER));

        // Create test song
        testSong = new Song();
        testSong.setId(1L);
        testSong.setSpotifyId("123456");
        testSong.setTitle("Test Song");
        testSong.setArtist("Test Artist");
        testSong.setAlbum("Test Album");
        testSong.setGenre("Pop");
        testSong.setDurationMs(180000);
        testSong.setSpotifyUrl("https://open.spotify.com/track/123456");
        testSong.setUsers(new HashSet<>());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")  // ✅ Use ADMIN role
    void assignSongToUser_ShouldReturnOk() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(songRepository.findById(testSong.getId())).thenReturn(Optional.of(testSong));

        mockMvc.perform(post("/api/music/{songId}/assign/{username}", testSong.getId(), "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("User assigned to song successfully."));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "SPOTIFY_USER")
    void saveSong_ShouldReturnCreatedStatus() throws Exception {
        MusicCreationDto songDto = new MusicCreationDto();
        songDto.setSpotifyId("654321");
        songDto.setTitle("New Song");
        songDto.setArtist("New Artist");
        songDto.setAlbum("New Album");
        songDto.setGenre("Pop");
        songDto.setDurationMs(210000);
        songDto.setSpotifyUrl("https://open.spotify.com/track/654321");

        SongDto mappedSongDto = new SongDto();
        mappedSongDto.setSpotifyId(songDto.getSpotifyId());
        mappedSongDto.setTitle(songDto.getTitle());
        mappedSongDto.setArtist(songDto.getArtist());
        mappedSongDto.setAlbum(songDto.getAlbum());
        mappedSongDto.setGenre(songDto.getGenre());
        mappedSongDto.setDurationMs(songDto.getDurationMs());
        mappedSongDto.setSpotifyUrl(songDto.getSpotifyUrl());

        when(songService.existsBySpotifyId(songDto.getSpotifyId())).thenReturn(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(songMapper.toEntity(songDto)).thenReturn(testSong);
        when(songService.saveSong(any(Song.class))).thenReturn(testSong);
        when(songMapper.toDto(any(Song.class))).thenReturn(mappedSongDto);

        mockMvc.perform(post("/api/music/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Song"))
                .andExpect(jsonPath("$.artist").value("New Artist"));

        verify(songService, times(1)).saveSong(any(Song.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}




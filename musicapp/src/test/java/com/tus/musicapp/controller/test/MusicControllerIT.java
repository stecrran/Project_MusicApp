package com.tus.musicapp.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
class MusicControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Song testSong;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
        userRepository.deleteAll();

        // Ensure test user exists before running the test
        testUser = userRepository.findByUsername("testuser").orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setPassword("password123");
            newUser.setRoles(Set.of(Role.SPOTIFY_USER));
            return userRepository.save(newUser);
        });

        // Ensure test song exists before running the test
        testSong = songRepository.findBySpotifyId("123456").orElseGet(() -> {
            Song newSong = new Song();
            newSong.setSpotifyId("123456");
            newSong.setTitle("Test Song");
            newSong.setArtist("Test Artist");
            newSong.setAlbum("Test Album");
            newSong.setGenre("Pop");
            newSong.setDurationMs(180000);
            newSong.setSpotifyUrl("https://open.spotify.com/track/123456");
            newSong.setUsers(new HashSet<>());
            return songRepository.save(newSong);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "SPOTIFY_USER")
    void getAllSongs_ShouldReturnListOfSongs() throws Exception {
        mockMvc.perform(get("/api/music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Song"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void assignSongToNonExistentUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/music/" + testSong.getId() + "/assign/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User or song not found."));
    }
}

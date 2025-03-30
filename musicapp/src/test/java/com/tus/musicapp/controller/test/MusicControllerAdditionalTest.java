package com.tus.musicapp.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.musicapp.controller.MusicController;
import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.mapper.SongMapper;
import com.tus.musicapp.mapper.SongMapperImpl;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.MusicRepository;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicController.class)
@Import(SongMapperImpl.class)
public class MusicControllerAdditionalTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SongService songService;
	@MockBean
	private SongRepository songRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private MusicRepository musicRepository;
	@Autowired
	private SongMapper songMapper;

	@BeforeEach
	void setupAuth() {
		Authentication auth = Mockito.mock(Authentication.class);
		SecurityContext context = Mockito.mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("testuser");
		SecurityContextHolder.setContext(context);
	}

	@Test
	void getUserSongs_ShouldReturnSongDtoList() throws Exception {
		Song song = new Song();
		song.setId(1L);
		song.setTitle("Song A");
		song.setGenre("Pop");

		User user = new User();
		user.setUsername("testuser");
		user.setSongCollection(new HashSet<>(List.of(song)));

		song.setUsers(Set.of(user));
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		mockMvc.perform(get("/api/music/my-songs")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Song A"))
				.andExpect(jsonPath("$[0].users[0]").value("testuser"));
	}

	@Test
	void saveSong_ShouldReturnConflictIfAlreadyExists() throws Exception {
		MusicCreationDto dto = new MusicCreationDto();
		dto.setSpotifyId("spotify123");
		dto.setTitle("Test Song");
		dto.setArtist("Test Artist");
		dto.setAlbum("Test Album");
		dto.setGenre("Pop");
		dto.setDurationMs(180000);
		dto.setSpotifyUrl("https://open.spotify.com/track/spotify123");

		when(songService.existsBySpotifyId("spotify123")).thenReturn(true);

		mockMvc.perform(post("/api/music/save").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(dto))).andExpect(status().isConflict())
				.andExpect(content().string(containsString("already exists")));
	}

	@Test
	void deleteSongById_ShouldDeleteSuccessfully() throws Exception {
		Song song = new Song();
		song.setId(1L);

		when(songService.findById(1L)).thenReturn(Optional.of(song));
		doNothing().when(songService).deleteById(1L);

		mockMvc.perform(delete("/api/music/1")).andExpect(status().isOk())
				.andExpect(content().string("Song deleted successfully."));
	}

	@Test
	void deleteSongById_ShouldReturnNotFound() throws Exception {
		when(songService.findById(99L)).thenReturn(Optional.empty());

		mockMvc.perform(delete("/api/music/99")).andExpect(status().isNotFound())
				.andExpect(content().string("Song not found."));
	}

	@Test
	void getGenreUsers_ShouldReturnCorrectMap() throws Exception {
		User user1 = new User();
		user1.setUsername("user1");
		User user2 = new User();
		user2.setUsername("user2");

		Song song1 = new Song();
		song1.setGenre("Jazz");
		song1.setUsers(Set.of(user1, user2));
		Song song2 = new Song();
		song2.setGenre("Pop");
		song2.setUsers(Set.of(user1));
		Song song3 = new Song();
		song3.setGenre("Jazz");
		song3.setUsers(Set.of(user1));

		when(songRepository.findAll()).thenReturn(List.of(song1, song2, song3));

		mockMvc.perform(get("/api/music/genre-users")).andExpect(status().isOk()).andExpect(jsonPath("$.Jazz").value(2))
				.andExpect(jsonPath("$.Pop").value(1));
	}
}

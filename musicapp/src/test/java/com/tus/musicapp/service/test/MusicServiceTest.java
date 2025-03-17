package com.tus.musicapp.service.test;

import com.tus.musicapp.model.Music;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.MusicRepository;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.MusicService;

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
class MusicServiceTest {

    @Mock
    private MusicRepository musicRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private MusicService musicService;

    private User user;
    private Music music;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setMusicCollection(new HashSet<>());

        music = new Music();
        music.setId(1L);
        music.setTitle("Test Music");
    }

    @Test
    void getAllMusic_ShouldReturnMusicList() {
        when(musicRepo.findAll()).thenReturn(List.of(music));

        List<Music> response = musicService.getAllMusic();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("Test Music", response.get(0).getTitle());
        verify(musicRepo, times(1)).findAll();
    }

    @Test
    void getUserMusic_ShouldReturnUserMusicCollection() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        user.getMusicCollection().add(music);
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        Set<Music> response = musicService.getUserMusic();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertTrue(response.contains(music));
    }

    @Test
    void addMusic_ShouldSaveMusicAndAssociateWithUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(musicRepo.save(music)).thenReturn(music);

        Music response = musicService.addMusic(music);

        assertNotNull(response);
        assertEquals("Test Music", response.getTitle());
        assertTrue(user.getMusicCollection().contains(music));
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void addMusicToUser_ShouldAssociateMusicWithUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(musicRepo.findById(1L)).thenReturn(Optional.of(music));

        musicService.addMusicToUser(1L);

        assertTrue(user.getMusicCollection().contains(music));
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void assignUserToMusic_ShouldReturnTrue_WhenUserAndMusicExist() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(musicRepo.findById(1L)).thenReturn(Optional.of(music));

        boolean result = musicService.assignUserToMusic(1L, "testuser");

        assertTrue(result);
        assertTrue(user.getMusicCollection().contains(music));
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void assignUserToMusic_ShouldReturnFalse_WhenUserOrMusicDoesNotExist() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());

        boolean result = musicService.assignUserToMusic(1L, "testuser");

        assertFalse(result);
    }
}

package com.tus.musicapp.mapper.test;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.mapper.SongMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SongMapperTest {

    private SongMapper songMapper;

    @BeforeEach
    void setUp() {
        songMapper = Mappers.getMapper(SongMapper.class);
    }

    @Test
    void toDto_ShouldMapSongToSongDto() {
        Song song = new Song();
        song.setId(1L);
        song.setTitle("Test Song");
        
        User user = new User();
        user.setUsername("testuser");
        Set<User> users = new HashSet<>();
        users.add(user);
        song.setUsers(users);

        SongDto songDto = songMapper.toDto(song);

        assertNotNull(songDto);
        assertEquals(1L, songDto.getId());
        assertEquals("Test Song", songDto.getTitle());
        assertNotNull(songDto.getUsers());
        assertEquals(1, songDto.getUsers().size());
        assertEquals("testuser", songDto.getUsers().get(0));
    }

    @Test
    void toEntity_ShouldMapMusicCreationDtoToSong() {
        MusicCreationDto musicCreationDto = new MusicCreationDto();
        musicCreationDto.setSpotifyId("123");
        musicCreationDto.setTitle("New Song");
        musicCreationDto.setArtist("Test Artist");
        musicCreationDto.setAlbum("Test Album");
        musicCreationDto.setGenre("Rock");
        musicCreationDto.setDurationMs(200000);
        musicCreationDto.setSpotifyUrl("https://spotify.com/test");

        Song song = songMapper.toEntity(musicCreationDto);

        assertNotNull(song);
        assertEquals("123", song.getSpotifyId());
        assertEquals("New Song", song.getTitle());
        assertEquals("Test Artist", song.getArtist());
        assertEquals("Test Album", song.getAlbum());
        assertEquals("Rock", song.getGenre());
        assertEquals(200000, song.getDurationMs());
        assertEquals("https://spotify.com/test", song.getSpotifyUrl());
    }
}

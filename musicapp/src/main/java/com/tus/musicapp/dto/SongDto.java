package com.tus.musicapp.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/*
 * DTO for representing song details.
 * Used to transfer song-related data between back-end and front-end,
 * this provides structure for API communication
 */
@Data
@Getter
@Setter
public class SongDto {
    private Long id;
    private String spotifyId;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationMs;
    private String spotifyUrl;
    
    // List of usernames of users who have added the song
    private List<String> users;
}

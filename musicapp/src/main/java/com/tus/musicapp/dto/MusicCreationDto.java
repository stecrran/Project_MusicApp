package com.tus.musicapp.dto;

import lombok.Data;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MusicCreationDto {

    @NotBlank(message = "Spotify ID is required")
    private String spotifyId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Artist is required")
    private String artist;

    @NotBlank(message = "Album is required")
    private String album;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Duration (ms) is required")
    private Integer durationMs;

    @NotBlank(message = "Spotify URL is required")
    private String spotifyUrl;
    
    private Set<String> usernames; // ✅ List of users who will be associated with this music
}

package com.tus.musicapp.dto;

import lombok.Data;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/* 
 * DTO for creating a new music entry. Used to capture the necessary details when a 
 * user wants to create a new music entry in the system (Link Spotify song to user). 
 * It ensures that required fields are validated before processing.
*/
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
    
    private Set<String> usernames; // List of users who will be associated with this music (multiple users, multiple tracks)
}

package com.tus.musicapp.dto;

import lombok.Data;

@Data
public class SongDto {
    private Long id;
    private String spotifyId;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationMs;
    private String spotifyUrl;
}

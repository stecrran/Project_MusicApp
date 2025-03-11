package com.tus.musicapp.model;

import javax.persistence.*;

@Entity
@Table(name = "music") // Ensure this matches your MySQL table name
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotifyId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String album;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int durationMs;

    @Column(nullable = false)
    private String spotifyUrl;

    // ✅ Default Constructor (Required by JPA)
    public Song() {}

    // ✅ Constructor Matching Expected Parameters
    public Song(String spotifyId, String title, String artist, String album, String genre, int durationMs, String spotifyUrl) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationMs = durationMs;
        this.spotifyUrl = spotifyUrl;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }
}

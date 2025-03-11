package com.tus.musicapp.model;

import javax.persistence.*;

@Entity
@Table(name = "music")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String album;
    private String artist;
    private String genre;
    private String spotifyId;

    // ✅ Default Constructor (Required by JPA)
    public Song() {
    }

    // ✅ New Constructor (Fixes the Error)
    public Song(String title, String album, String artist, String genre, String spotifyId) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.genre = genre;
        this.spotifyId = spotifyId;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
}

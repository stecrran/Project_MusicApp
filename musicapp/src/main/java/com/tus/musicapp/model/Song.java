package com.tus.musicapp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

/* 
 * JPA entity used for managing user song collections. 
 * Establishes a Many-to-Many relationship with the User entity (songCollection)
 */
@Getter
@Setter
@Entity
@Table(name = "music") 
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
    
    @ManyToMany(mappedBy = "songCollection", fetch = FetchType.LAZY)
    @JsonBackReference  // Prevents infinite recursion (songs have users, users have songs)
    private Set<User> users;

    // Default Constructor (Required by JPA)
    public Song() {}

    // Constructor Matching Expected Parameters
    public Song(String spotifyId, String title, String artist, String album, String genre, int durationMs, String spotifyUrl) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationMs = durationMs;
        this.spotifyUrl = spotifyUrl;
    }

}

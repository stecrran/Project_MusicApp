package com.tus.musicapp.model;

import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

// JPA entity representing a music object stored in the database
@Entity
@Getter
@Setter
@Table(name = "music") 
public class Music {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(nullable = false, unique = true)
    private String spotifyId; 

	@Column(nullable = false)
	private String album;

	@Column(nullable = false)
	private String artist;

	@Column(nullable = false)
	private String genre;

	@Column(nullable = false)
	private String title;
	
    @Column(nullable = false)
    private int durationMs;

    @Column(nullable = false)
    private String spotifyUrl;
	
    @ManyToMany(mappedBy = "musicCollection")
    private Set<User> users; // Users who have this song in their collection

	public Music() {
	}

    public Music(String spotifyId, String title, String artist, String album, String genre, int durationMs, String spotifyUrl) {
        this.spotifyId = spotifyId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.durationMs = durationMs;
        this.spotifyUrl = spotifyUrl;
    }

}

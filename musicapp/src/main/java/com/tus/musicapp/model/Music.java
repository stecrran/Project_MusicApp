package com.tus.musicapp.model;

import javax.persistence.*;

@Entity
@Table(name = "music") // Ensure the table name matches your database
public class Music {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String album;

	@Column(nullable = false)
	private String artist;

	@Column(nullable = false)
	private String genre;

	@Column(nullable = false)
	private String title;

	// Constructors
	public Music() {
	}

	public Music(String album, String artist, String genre, String title) {
		this.album = album;
		this.artist = artist;
		this.genre = genre;
		this.title = title;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

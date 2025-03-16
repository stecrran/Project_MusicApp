package com.tus.musicapp.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tus.musicapp.model.Music;
import com.tus.musicapp.model.User;
import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    // ✅ Find by Spotify ID (since it's unique)
    Optional<Music> findBySpotifyId(String spotifyId);

    // ✅ Find by Title
    List<Music> findByTitleContainingIgnoreCase(String title);
    
    // ✅ Find by Artist
    List<Music> findByArtistContainingIgnoreCase(String artist);
    
    // ✅ Find by Genre
    List<Music> findByGenreContainingIgnoreCase(String genre);
}

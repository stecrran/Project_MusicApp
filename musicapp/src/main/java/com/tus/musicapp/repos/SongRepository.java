package com.tus.musicapp.repos;

import com.tus.musicapp.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Spring Data JPA repository. Interface responsible for handling database interactions related to the Song entity
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Check if a song exists by Spotify ID (Prevents duplicates)
    boolean existsBySpotifyId(String spotifyId);

    // Retrieve a song by Spotify ID (Optional to handle non-existing cases)
    Optional<Song> findBySpotifyId(String spotifyId);
}

package com.tus.musicapp.controller;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    @Autowired
    private SongRepository songRepository;

    // ✅ Get all songs from the database
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        return ResponseEntity.ok(songs);
    }

    // ✅ Save a song (if it doesn't exist)
    @PostMapping("/save")
    public ResponseEntity<?> saveSong(@RequestBody Song song) {
        try {
            if (songRepository.existsBySpotifyId(song.getSpotifyId())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "🎵 Song already exists in the database.");
                return ResponseEntity.ok(response);
            }

            // ✅ Ensure the new song is properly constructed
            Song newSong = new Song(
                song.getSpotifyId(),
                song.getTitle(),
                song.getArtist(),
                song.getAlbum(),
                song.getGenre(),
                song.getDurationMs(),
                song.getSpotifyUrl()
            );

            Song savedSong = songRepository.save(newSong);
            return ResponseEntity.ok(savedSong);

        } catch (Exception e) {
            // ✅ Log error and return 500 response
            System.err.println("❌ Error saving song: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    // ✅ Delete a song by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        try {
            if (!songRepository.existsById(id)) {
                return ResponseEntity.status(404).body("❌ Song not found.");
            }

            songRepository.deleteById(id);
            return ResponseEntity.ok("🗑 Song deleted successfully.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Error deleting song: " + e.getMessage());
        }
    }
}

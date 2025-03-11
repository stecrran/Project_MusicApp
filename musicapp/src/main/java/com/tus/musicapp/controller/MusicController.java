package com.tus.musicapp.controller;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    @Autowired
    private SongRepository songRepository;

    @PostMapping("/save")
    public ResponseEntity<?> saveSong(@RequestBody Song song) {
        if (songRepository.existsBySpotifyId(song.getSpotifyId())) {
            return ResponseEntity.ok("🎵 Song already exists in the database.");
        }

        Song savedSong = songRepository.save(song);
        return ResponseEntity.ok(savedSong);
    }
}

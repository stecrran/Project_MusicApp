package com.tus.musicapp.controller;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
public class SpotifyController {

    private final SongRepository songRepository;

    // Constructor Injection
    @Autowired
    public SpotifyController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // Fetch all saved songs from the database
    @GetMapping("/all")
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
}

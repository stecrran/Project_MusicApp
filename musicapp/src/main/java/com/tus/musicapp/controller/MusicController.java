package com.tus.musicapp.controller;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.mapper.SongMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final SongService songService;
    private final SongMapper songMapper;

    public MusicController(SongService songService, SongMapper songMapper) {
        this.songService = songService;
        this.songMapper = songMapper;
    }

    // ✅ Get all songs using DTOs
    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        List<SongDto> songs = songService.findAll()
                                         .stream()
                                         .map(songMapper::toDto)
                                         .collect(Collectors.toList());
        return ResponseEntity.ok(songs); 

    }

    // ✅ Save a song using DTO
    @PostMapping("/save")
    public ResponseEntity<?> saveSong(@Valid @RequestBody MusicCreationDto musicCreationDto) {
        try {
            if (songService.existsBySpotifyId(musicCreationDto.getSpotifyId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("🎵 Song already exists in the database.");
            }

            Song song = songMapper.toEntity(musicCreationDto);
            Song savedSong = songService.saveSong(song);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(songMapper.toDto(savedSong));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("❌ Error saving song: " + e.getMessage());
        }
    }

    // ✅ Delete a song by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        try {
            if (!songService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("❌ Song not found.");
            }
            songService.deleteById(id);
            return ResponseEntity.ok("✅ Song deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("❌ Error deleting song: " + e.getMessage());
        }
    }
}

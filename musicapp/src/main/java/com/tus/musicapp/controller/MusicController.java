package com.tus.musicapp.controller;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.mapper.SongMapper;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;
import java.util.function.Function;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final SongService songService;
    private final SongMapper songMapper;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public MusicController(SongService songService, SongMapper songMapper, SongRepository songRepository, UserRepository userRepository) {
        this.songService = songService;
        this.songMapper = songMapper;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }


    // Get all songs using DTO
    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        List<SongDto> songs = songService.findAll()
                                         .stream()
                                         .map(songMapper::toDto)
                                         .collect(Collectors.toList());
        return ResponseEntity.ok(songs); 

    }
    
    // Get songs added by the logged-in user
    @GetMapping("/my-songs")
    public ResponseEntity<List<SongDto>> getUserSongs() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SongDto> userSongs = user.getSongCollection()
                .stream()
                .map(song -> {
                    SongDto songDto = songMapper.toDto(song);

                    // Add usernames of users who have this song
                    songDto.setUsers(song.getUsers().stream()
                            .map(User::getUsername)
                            .collect(Collectors.toList()));

                    return songDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userSongs);
    }



    // Save a song using DTO
    @PostMapping("/save")
    public ResponseEntity<?> saveSong(@Valid @RequestBody MusicCreationDto musicCreationDto) {
        try {
            if (songService.existsBySpotifyId(musicCreationDto.getSpotifyId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Song already exists in the database.");
            }

            // Get the logged-in user
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Song song = songMapper.toEntity(musicCreationDto);
            Song savedSong = songService.saveSong(song);

            // Ensure the song is not already in the user's collection
            if (!user.getSongCollection().contains(savedSong)) {
                user.getSongCollection().add(savedSong);
                userRepository.save(user);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(songMapper.toDto(savedSong));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                                 .body("Error saving song: " + e.getMessage());
        }
    }
    
    // Add a song to a specific user (Admin Only) 
    @PreAuthorize("hasRole('ADMIN')") // Restrict to Admin users
    @PostMapping("/{songId}/assign/{username}")
    public ResponseEntity<?> assignSongToUser(@PathVariable Long songId, @PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Song> songOpt = songRepository.findById(songId);

        if (userOpt.isPresent() && songOpt.isPresent()) {
            User user = userOpt.get();
            Song song = songOpt.get();

            if (!user.getSongCollection().contains(song)) {
                user.getSongCollection().add(song);
                userRepository.save(user);
                return ResponseEntity.ok("User assigned to song successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User already has this song in their collection.");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User or song not found.");
    }
    
    // Remove a song from the user's collection
    @DeleteMapping("/remove/{songId}")
    public ResponseEntity<?> removeSongFromUser(@PathVariable Long songId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            
            if (!user.getSongCollection().contains(song)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("You do not have this song in your collection.");
            }

            user.getSongCollection().remove(song);
            userRepository.save(user);
            return ResponseEntity.ok("Song removed from your collection.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Song not found.");
    }


    // Delete a song by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        try {
            Optional<Song> songOpt = songService.findById(id);
            if (songOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Song not found.");
            }

            songService.deleteById(id);
            return ResponseEntity.ok("Song deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error deleting song: " + e.getMessage());
        }
    }


    
    // Get Genre Count (using 'SongService')
    @GetMapping("/genre-count")
    public ResponseEntity<Map<String, Long>> getGenreCount() {
        List<Song> songs = songRepository.findAll();

        // Handle nulls, split genres, and count occurrences
        Map<String, Long> genreCount = songs.stream()
            .map(Song::getGenre)  // Extract genre string
            .filter(Objects::nonNull) // Avoid NullPointerException
            .flatMap(genre -> Arrays.stream(genre.split(",\\s*"))) // Ensure correct Stream<String>
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return ResponseEntity.ok(genreCount);
    }

    @GetMapping("/genre-users")
    public ResponseEntity<Map<String, Integer>> getGenreUserCount() {
        List<Song> songs = songRepository.findAll();

        // Map of genre to unique user count
        Map<String, Set<String>> genreUserMap = new HashMap<>();

        for (Song song : songs) {
            if (song.getGenre() != null && !song.getUsers().isEmpty()) {
                String[] genres = song.getGenre().split(",\\s*"); // Handle multiple genres
                for (String genre : genres) {
                    genreUserMap.putIfAbsent(genre, new HashSet<>());
                    for (User user : song.getUsers()) {
                        genreUserMap.get(genre).add(user.getUsername()); // Store unique usernames
                    }
                }
            }
        }

        // Convert to a map of genre to unique user count
        Map<String, Integer> genreUserCount = genreUserMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()));

        return ResponseEntity.ok(genreUserCount);
    }


}

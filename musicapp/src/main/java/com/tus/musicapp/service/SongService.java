package com.tus.musicapp.service;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.SongRepository;
import com.tus.musicapp.repos.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Responsible for handling song-related operations in the application. 
 * It provides methods for retrieving, saving, deleting, assigning songs to users, 
 * and generating genre-based statistics
 */
@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SongService(SongRepository songRepo, UserRepository userRepo) {
        this.songRepository = songRepo;
        this.userRepository = userRepo;
    }

    public List<Song> findAll() {
        return songRepository.findAll();
    }

    public boolean existsBySpotifyId(String spotifyId) {
        return songRepository.existsBySpotifyId(spotifyId);
    }

    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    public Song saveSong(Song song) {
        return songRepository.save(song);
    }

    public void deleteById(Long id) {
        songRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return songRepository.existsById(id);
    }
    
    public Map<String, Long> getGenreCount() {
        return songRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(Song::getGenre, Collectors.counting()));
    }
    
    // get songs associated with user
    public Set<Song> getUserSongs() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
        	    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user.getSongCollection();
    }

    public void addSongToUser(Long songId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        user.getSongCollection().add(song);
        userRepository.save(user);
    }
    
}

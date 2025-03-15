package com.tus.musicapp.service;

import com.tus.musicapp.model.Song;
import com.tus.musicapp.repos.SongRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
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
    
}

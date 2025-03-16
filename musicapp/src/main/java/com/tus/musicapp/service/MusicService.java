package com.tus.musicapp.service;

import com.tus.musicapp.model.Music;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.MusicRepository;
import com.tus.musicapp.repos.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MusicService {

    private final MusicRepository musicRepo;
    private final UserRepository userRepo;

    public MusicService(MusicRepository musicRepo, UserRepository userRepo) {
        this.musicRepo = musicRepo;
        this.userRepo = userRepo;
    }

    // Get all songs (publicly available)
    public List<Music> getAllMusic() {
        return musicRepo.findAll();
    }

    // Get music added by the logged-in user
    public Set<Music> getUserMusic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getMusicCollection();
    }

    // ✅ Add a song to the database and associate it with the logged-in user
    public Music addMusic(Music music) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Save music first
        Music savedMusic = musicRepo.save(music);

        // Associate the logged-in user with the music
        if (user.getMusicCollection() == null) {
            user.setMusicCollection(new HashSet<>());
        }
        user.getMusicCollection().add(savedMusic);
        userRepo.save(user);

        return savedMusic;
    }

    // Add a song to the logged-in user's collection
    public void addMusicToUser(Long musicId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Music music = musicRepo.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        if (user.getMusicCollection() == null) {
            user.setMusicCollection(new HashSet<>());
        }
        user.getMusicCollection().add(music);

        userRepo.save(user);
    }
    
    // ✅ Admin assigns a user to a song (via API)
    public boolean assignUserToMusic(Long musicId, String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        Optional<Music> musicOpt = musicRepo.findById(musicId);

        if (userOpt.isPresent() && musicOpt.isPresent()) {
            User user = userOpt.get();
            Music music = musicOpt.get();

            if (!user.getMusicCollection().contains(music)) {
                user.getMusicCollection().add(music);
                userRepo.save(user);
                return true;
            }
        }
        return false; // User or music not found
    }
}

package com.tus.musicapp.service;

import com.tus.musicapp.models.Music;
import com.tus.musicapp.repos.MusicRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MusicService {

	private final MusicRepository musicRepo;
	
	public MusicService(MusicRepository musicRepo) {
		this.musicRepo = musicRepo;
	}
	
	public List<Music> getAllMusic() {
		return musicRepo.findAll();
	}
	
	public Music addMusic(Music music) {
		return musicRepo.save(music);
	}
	
}

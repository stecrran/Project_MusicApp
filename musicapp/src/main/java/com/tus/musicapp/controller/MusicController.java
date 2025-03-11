package com.tus.musicapp.controller;

import com.tus.musicapp.model.Music;
import com.tus.musicapp.service.MusicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/music")
public class MusicController {

	private final MusicService musicService;
	
	public MusicController(MusicService musicService) {
		this.musicService = musicService;
	}
	
	@GetMapping
	public List<Music> getMusic() {
		return musicService.getAllMusic();
	}
	
	@PostMapping
	public Music addMusic(@RequestBody Music music) {
		return musicService.addMusic(music);
	}
}

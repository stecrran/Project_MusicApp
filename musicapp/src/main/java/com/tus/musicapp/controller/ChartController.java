package com.tus.musicapp.controller;

import com.tus.musicapp.dto.ArtistDTO;
import com.tus.musicapp.service.LastFmService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChartController {

    private final LastFmService lastFmService;

    public ChartController(LastFmService lastFmService) {
        this.lastFmService = lastFmService;
    }

    @GetMapping("/charts")
    public String displayCharts(Model model) {
        List<ArtistDTO> topArtists = lastFmService.getTopArtists();
        model.addAttribute("artists", topArtists);
        return "charts"; // Thymeleaf template html
    }
}


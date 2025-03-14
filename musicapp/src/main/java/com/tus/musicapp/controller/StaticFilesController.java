package com.tus.musicapp.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StaticFilesController {

	// Manage images
    @GetMapping("/carousel-images")
    public List<String> getCarouselImages() throws IOException {
        File folder = new ClassPathResource("static/assets/images/carousel/").getFile();

        if (!folder.exists() || !folder.isDirectory()) {
            return List.of(); // Return empty list if directory is missing
        }

        return Arrays.stream(folder.listFiles())
                .filter(file -> file.isFile() && file.getName().matches(".*\\.(jpg|jpeg|png|gif)$")) // Only images
                .map(file -> "/assets/images/carousel/" + file.getName()) // Convert to URL path
                .collect(Collectors.toList());
    }
}

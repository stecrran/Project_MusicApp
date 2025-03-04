package com.tus.musicapp.service;

import com.tus.musicapp.dto.ArtistDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LastFmService {

    private static final String API_KEY = "6b60a86cd683cb79b5539cb86fbccdf6"; 
    private static final String LAST_FM_URL = "http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key="
            + API_KEY + "&format=json";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LastFmService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public List<ArtistDTO> getTopArtists() {
        try {
            String response = restTemplate.getForObject(LAST_FM_URL, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode artistsNode = root.path("artists").path("artist");
            List<ArtistDTO> artistList = new ArrayList<>();

            if (artistsNode.isArray()) {
                for (JsonNode artistNode : artistsNode) {
                    ArtistDTO artist = new ArtistDTO();
                    artist.setName(artistNode.path("name").asText());
                    artist.setPlaycount(artistNode.path("playcount").asLong());
                    artist.setListeners(artistNode.path("listeners").asLong());
                    artist.setUrl(artistNode.path("url").asText());
                    artistList.add(artist);
                }
            }
            return artistList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from Last.fm", e);
        }
    }
}

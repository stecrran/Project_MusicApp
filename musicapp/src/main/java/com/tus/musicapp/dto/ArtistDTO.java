package com.tus.musicapp.dto;


public class ArtistDTO {
    private String name;
    private long playcount;
    private long listeners;
    private String url;

    // Default constructor
    public ArtistDTO() {
    }

    // Parameterized constructor
    public ArtistDTO(String name, long playcount, long listeners, String url) {
        this.name = name;
        this.playcount = playcount;
        this.listeners = listeners;
        this.url = url;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPlaycount() {
        return playcount;
    }

    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    public long getListeners() {
        return listeners;
    }

    public void setListeners(long listeners) {
        this.listeners = listeners;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}


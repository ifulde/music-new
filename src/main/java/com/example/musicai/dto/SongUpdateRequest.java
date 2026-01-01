package com.example.musicai.dto;

import lombok.Data;

@Data
public class SongUpdateRequest {
    private String title;
    private String artist;
    private String album;
}


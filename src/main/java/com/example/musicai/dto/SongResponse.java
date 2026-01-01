package com.example.musicai.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SongResponse {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private Integer duration;
    private String coverPath;
    private Integer playCount;
    private Long uploaderId;
    private LocalDateTime createdAt;
}


package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlaylistSong {
    private Long id;
    private Long playlistId;
    private Long songId;
    private LocalDateTime createdAt;
}


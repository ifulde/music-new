package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Playlist {
    private Long id;
    private String name;
    private String description;
    private Long userId; // 创建者ID
    private LocalDateTime createdAt;
}


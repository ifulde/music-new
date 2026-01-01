package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long songsId; // 注意：数据库字段是songs_id
    private LocalDateTime createdAt;
}


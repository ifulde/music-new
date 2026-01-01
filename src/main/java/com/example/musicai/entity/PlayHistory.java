package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistory {
    private Long id;
    private Long userId;
    private Long songId;
    private LocalDateTime playedAt;
}


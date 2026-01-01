package com.example.musicai.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistoryResponse {
    private Long id;
    private Long userId;
    private Long songId;
    private SongResponse song;
    private LocalDateTime playedAt;
}


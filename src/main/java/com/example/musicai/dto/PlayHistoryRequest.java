package com.example.musicai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistoryRequest {
    @NotNull(message = "歌曲ID不能为空")
    private Long songId;
    private LocalDateTime playedAt;
}


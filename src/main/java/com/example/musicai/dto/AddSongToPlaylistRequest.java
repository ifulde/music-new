package com.example.musicai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddSongToPlaylistRequest {
    @NotNull(message = "歌曲ID不能为空")
    private Long songId;
}


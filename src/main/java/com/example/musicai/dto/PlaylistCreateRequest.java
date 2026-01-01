package com.example.musicai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaylistCreateRequest {
    @NotBlank(message = "播放列表名称不能为空")
    private String name;
    private String description;
}


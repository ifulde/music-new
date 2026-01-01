package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Song {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private Integer duration; // 时长（秒）
    private String filePath; // 音频文件存储路径
    private String coverPath; // 封面图片存储路径
    private Integer playCount; // 播放次数
    private Long uploaderId; // 上传者ID
    private LocalDateTime createdAt;
}


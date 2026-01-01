package com.example.musicai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.audio-dir}")
    private String audioDir;

    @Value("${file.upload.cover-dir}")
    private String coverDir;

    @PostConstruct
    public void init() {
        // 创建必要的目录
        new File(uploadDir).mkdirs();
        new File(audioDir).mkdirs();
        new File(coverDir).mkdirs();
    }
}


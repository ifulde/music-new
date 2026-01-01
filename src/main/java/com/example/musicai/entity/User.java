package com.example.musicai.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
}


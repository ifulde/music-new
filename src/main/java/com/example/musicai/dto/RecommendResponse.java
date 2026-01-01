package com.example.musicai.dto;

import lombok.Data;

@Data
public class RecommendResponse {
    private Integer code;
    private RecommendData data;
    private String msg;
    private String requestId;
}

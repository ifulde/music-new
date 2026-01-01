package com.example.musicai.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendData {
    private String recommendType;
    private Integer topKCount;
    private List<Long> topKItem;
    private List<Double> topKScore;
    private String userId;
}

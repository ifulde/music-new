package com.example.musicai.controller;

import com.example.musicai.dto.ApiResponse;
import com.example.musicai.service.RecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    private static final Logger logger = LoggerFactory.getLogger(RecommendController.class);

    @Autowired
    private RecommendService recommendService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<Long>>> getRecommendations(@RequestBody Object requestData) {
        logger.info("收到推荐请求，数据: {}", requestData);

        List<Long> topKItem = recommendService.getRecommendations(requestData);

        if (topKItem != null) {
            logger.info("成功获取推荐歌曲: {}", topKItem);
            return ResponseEntity.ok(ApiResponse.success("获取推荐成功", topKItem));
        } else {
            logger.error("获取推荐失败");
            return ResponseEntity.status(500).body(ApiResponse.error("获取推荐失败"));
        }
    }
}

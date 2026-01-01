package com.example.musicai.controller;

import com.example.musicai.dto.*;
import com.example.musicai.service.PlayHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    private PlayHistoryService playHistoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlayHistoryResponse>> recordPlayHistory(
            @Valid @RequestBody PlayHistoryRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PlayHistoryResponse history = playHistoryService.recordPlayHistory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("记录成功", history));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PlayHistoryResponse>>> getPlayHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResponse<PlayHistoryResponse> histories = playHistoryService.getUserPlayHistory(userId, page, limit);
        return ResponseEntity.ok(ApiResponse.success(histories));
    }
}


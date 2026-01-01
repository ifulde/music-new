package com.example.musicai.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int limit;
    private long total;
    private int totalPages;

    public PageResponse(List<T> content, int page, int limit, long total) {
        this.content = content;
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / limit);
    }
}


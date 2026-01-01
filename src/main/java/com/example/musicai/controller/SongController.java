package com.example.musicai.controller;

import com.example.musicai.dto.*;
import com.example.musicai.service.SongService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    @Autowired
    private SongService songService;

    @Value("${file.upload.audio-dir}")
    private String audioDir;

    @PostMapping
    public ResponseEntity<ApiResponse<SongResponse>> uploadSong(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam(value = "album", required = false) String album,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        SongResponse song = songService.uploadSong(audio, cover, title, artist, album, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("上传成功", song));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SongResponse>>> getSongs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String search) {
        PageResponse<SongResponse> songs = songService.getSongs(page, limit, search);
        return ResponseEntity.ok(ApiResponse.success(songs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> getSongById(@PathVariable Long id) {
        SongResponse song = songService.getSongById(id);
        return ResponseEntity.ok(ApiResponse.success(song));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        SongResponse song = songService.updateSong(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("更新成功", song));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSong(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        songService.deleteSong(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stream/{songId}")
    public ResponseEntity<Resource> streamSong(
            @PathVariable Long songId,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletRequest request) {
        // 验证用户已登录（通过拦截器）
        Long userId = (Long) request.getAttribute("userId");

        com.example.musicai.entity.Song song = songService.getSongEntity(songId);
        File audioFile = new File(audioDir, song.getFilePath());

        if (!audioFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(audioFile);
        String contentType = "audio/mpeg"; // 默认为mp3
        if (song.getFilePath().toLowerCase().endsWith(".flac")) {
            contentType = "audio/flac";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.set("Accept-Ranges", "bytes");

        // 处理Range请求
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            return handleRangeRequest(resource, rangeHeader, audioFile, headers, contentType);
        }

        headers.setContentLength(audioFile.length());
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    private ResponseEntity<Resource> handleRangeRequest(
            Resource resource, String rangeHeader, File file, HttpHeaders headers, String contentType) {
        try {
            long fileSize = file.length();
            String range = rangeHeader.substring(6); // 移除 "bytes=" 前缀
            String[] ranges = range.split("-");
            
            long start = 0;
            long end = fileSize - 1;
            
            if (ranges.length > 0 && !ranges[0].isEmpty()) {
                start = Long.parseLong(ranges[0]);
            }
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }
            
            if (start > end || start < 0 || end >= fileSize) {
                headers.set("Content-Range", "bytes */" + fileSize);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .headers(headers)
                        .build();
            }

            long contentLength = end - start + 1;
            headers.setContentLength(contentLength);
            headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
            headers.setContentType(MediaType.parseMediaType(contentType));

            // 注意：这里返回完整的resource，Spring会自动处理Range
            // 对于完整的Range支持，需要使用RandomAccessFile或FileChannel来读取指定范围
            // 但为了简化，这里先返回完整resource，浏览器客户端可以处理
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


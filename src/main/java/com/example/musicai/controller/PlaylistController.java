package com.example.musicai.controller;

import com.example.musicai.dto.*;
import com.example.musicai.service.PlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaylistResponse>> createPlaylist(
            @Valid @RequestBody PlaylistCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PlaylistResponse playlist = playlistService.createPlaylist(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建成功", playlist));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaylistResponse>>> getUserPlaylists(
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PlaylistResponse> playlists = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(ApiResponse.success(playlists));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> getPlaylistById(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PlaylistResponse playlist = playlistService.getPlaylistById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(playlist));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody PlaylistUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PlaylistResponse playlist = playlistService.updatePlaylist(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("更新成功", playlist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePlaylist(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.deletePlaylist(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<ApiResponse<List<SongResponse>>> addSongToPlaylist(
            @PathVariable Long playlistId,
            @Valid @RequestBody AddSongToPlaylistRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<SongResponse> songs = playlistService.addSongToPlaylist(playlistId, request.getSongId(), userId);
        return ResponseEntity.ok(ApiResponse.success("添加成功", songs));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<ApiResponse<List<SongResponse>>> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<SongResponse> songs = playlistService.removeSongFromPlaylist(playlistId, songId, userId);
        return ResponseEntity.ok(ApiResponse.success("移除成功", songs));
    }
}


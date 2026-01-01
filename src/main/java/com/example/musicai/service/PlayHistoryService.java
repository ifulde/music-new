package com.example.musicai.service;

import com.example.musicai.dto.PageResponse;
import com.example.musicai.dto.PlayHistoryRequest;
import com.example.musicai.dto.PlayHistoryResponse;
import com.example.musicai.dto.SongResponse;
import com.example.musicai.entity.PlayHistory;
import com.example.musicai.entity.Song;
import com.example.musicai.mapper.PlayHistoryMapper;
import com.example.musicai.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayHistoryService {
    @Autowired
    private PlayHistoryMapper playHistoryMapper;

    @Autowired
    private SongMapper songMapper;

    @Transactional
    public PlayHistoryResponse recordPlayHistory(PlayHistoryRequest request, Long userId) {
        // 验证歌曲是否存在
        Song song = songMapper.findById(request.getSongId());
        if (song == null) {
            throw new com.example.musicai.exception.ResourceNotFoundException("歌曲不存在");
        }

        PlayHistory playHistory = new PlayHistory();
        playHistory.setUserId(userId);
        playHistory.setSongId(request.getSongId());
        playHistory.setPlayedAt(request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now());

        playHistoryMapper.insert(playHistory);

        // 增加播放次数
        songMapper.incrementPlayCount(request.getSongId());

        return convertToResponse(playHistory, song);
    }

    public PageResponse<PlayHistoryResponse> getUserPlayHistory(Long userId, int page, int limit) {
        int offset = (page - 1) * limit;
        List<PlayHistory> histories = playHistoryMapper.findByUserId(userId, offset, limit);
        long total = playHistoryMapper.countByUserId(userId);

        List<PlayHistoryResponse> responses = histories.stream()
                .map(history -> {
                    Song song = songMapper.findById(history.getSongId());
                    return convertToResponse(history, song);
                })
                .collect(Collectors.toList());

        return new PageResponse<>(responses, page, limit, total);
    }

    private PlayHistoryResponse convertToResponse(PlayHistory history, Song song) {
        PlayHistoryResponse response = new PlayHistoryResponse();
        response.setId(history.getId());
        response.setUserId(history.getUserId());
        response.setSongId(history.getSongId());
        response.setPlayedAt(history.getPlayedAt());

        if (song != null) {
            SongResponse songResponse = new SongResponse();
            songResponse.setId(song.getId());
            songResponse.setTitle(song.getTitle());
            songResponse.setArtist(song.getArtist());
            songResponse.setAlbum(song.getAlbum());
            songResponse.setDuration(song.getDuration());
            songResponse.setCoverPath(song.getCoverPath());
            songResponse.setPlayCount(song.getPlayCount());
            songResponse.setUploaderId(song.getUploaderId());
            songResponse.setCreatedAt(song.getCreatedAt());
            response.setSong(songResponse);
        }

        return response;
    }
}


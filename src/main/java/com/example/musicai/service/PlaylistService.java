package com.example.musicai.service;

import com.example.musicai.dto.*;
import com.example.musicai.entity.Playlist;
import com.example.musicai.entity.PlaylistSong;
import com.example.musicai.entity.Song;
import com.example.musicai.exception.ResourceNotFoundException;
import com.example.musicai.exception.UnauthorizedException;
import com.example.musicai.mapper.PlaylistMapper;
import com.example.musicai.mapper.PlaylistSongMapper;
import com.example.musicai.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private PlaylistSongMapper playlistSongMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private SongService songService;

    @Transactional
    public PlaylistResponse createPlaylist(PlaylistCreateRequest request, Long userId) {
        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setUserId(userId);
        playlist.setCreatedAt(LocalDateTime.now());

        playlistMapper.insert(playlist);

        PlaylistResponse response = convertToResponse(playlist);
        response.setSongs(new ArrayList<>());
        return response;
    }

    public List<PlaylistResponse> getUserPlaylists(Long userId) {
        List<Playlist> playlists = playlistMapper.findByUserId(userId);
        return playlists.stream()
                .map(this::convertToResponseWithSongs)
                .collect(Collectors.toList());
    }

    public PlaylistResponse getPlaylistById(Long id, Long userId) {
        Playlist playlist = playlistMapper.findById(id);
        if (playlist == null) {
            throw new ResourceNotFoundException("播放列表不存在");
        }
        return convertToResponseWithSongs(playlist);
    }

    @Transactional
    public PlaylistResponse updatePlaylist(Long id, PlaylistUpdateRequest request, Long userId) {
        Playlist playlist = playlistMapper.findById(id);
        if (playlist == null) {
            throw new ResourceNotFoundException("播放列表不存在");
        }

        if (!playlist.getUserId().equals(userId)) {
            throw new UnauthorizedException("不是播放列表的所有者");
        }

        if (request.getName() != null) {
            playlist.setName(request.getName());
        }
        if (request.getDescription() != null) {
            playlist.setDescription(request.getDescription());
        }

        playlistMapper.update(playlist);
        return convertToResponseWithSongs(playlist);
    }

    @Transactional
    public void deletePlaylist(Long id, Long userId) {
        Playlist playlist = playlistMapper.findById(id);
        if (playlist == null) {
            throw new ResourceNotFoundException("播放列表不存在");
        }

        if (!playlist.getUserId().equals(userId)) {
            throw new UnauthorizedException("不是播放列表的所有者");
        }

        // 删除播放列表中的歌曲关联
        playlistSongMapper.deleteByPlaylistId(id);
        // 删除播放列表
        playlistMapper.deleteById(id);
    }

    @Transactional
    public List<SongResponse> addSongToPlaylist(Long playlistId, Long songId, Long userId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) {
            throw new ResourceNotFoundException("播放列表不存在");
        }

        if (!playlist.getUserId().equals(userId)) {
            throw new UnauthorizedException("不是播放列表的所有者");
        }

        Song song = songMapper.findById(songId);
        if (song == null) {
            throw new ResourceNotFoundException("歌曲不存在");
        }

        // 检查歌曲是否已经在播放列表中
        if (playlistSongMapper.findByPlaylistIdAndSongId(playlistId, songId) != null) {
            throw new IllegalArgumentException("歌曲已在播放列表中");
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylistId(playlistId);
        playlistSong.setSongId(songId);
        playlistSong.setCreatedAt(LocalDateTime.now());
        playlistSongMapper.insert(playlistSong);

        return getPlaylistSongs(playlistId);
    }

    @Transactional
    public List<SongResponse> removeSongFromPlaylist(Long playlistId, Long songId, Long userId) {
        Playlist playlist = playlistMapper.findById(playlistId);
        if (playlist == null) {
            throw new ResourceNotFoundException("播放列表不存在");
        }

        if (!playlist.getUserId().equals(userId)) {
            throw new UnauthorizedException("不是播放列表的所有者");
        }

        playlistSongMapper.deleteByPlaylistIdAndSongId(playlistId, songId);
        return getPlaylistSongs(playlistId);
    }

    private List<SongResponse> getPlaylistSongs(Long playlistId) {
        List<Long> songIds = playlistSongMapper.findSongIdsByPlaylistId(playlistId);
        return songIds.stream()
                .map(songMapper::findById)
                .map(song -> {
                    SongResponse response = new SongResponse();
                    response.setId(song.getId());
                    response.setTitle(song.getTitle());
                    response.setArtist(song.getArtist());
                    response.setAlbum(song.getAlbum());
                    response.setDuration(song.getDuration());
                    response.setCoverPath(song.getCoverPath());
                    response.setPlayCount(song.getPlayCount());
                    response.setUploaderId(song.getUploaderId());
                    response.setCreatedAt(song.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private PlaylistResponse convertToResponse(Playlist playlist) {
        PlaylistResponse response = new PlaylistResponse();
        response.setId(playlist.getId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setUserId(playlist.getUserId());
        response.setCreatedAt(playlist.getCreatedAt());
        return response;
    }

    private PlaylistResponse convertToResponseWithSongs(Playlist playlist) {
        PlaylistResponse response = convertToResponse(playlist);
        response.setSongs(getPlaylistSongs(playlist.getId()));
        return response;
    }
}


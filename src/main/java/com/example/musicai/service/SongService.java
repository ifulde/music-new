package com.example.musicai.service;

import com.example.musicai.dto.PageResponse;
import com.example.musicai.dto.SongResponse;
import com.example.musicai.dto.SongUpdateRequest;
import com.example.musicai.entity.Song;
import com.example.musicai.exception.ResourceNotFoundException;
import com.example.musicai.exception.UnauthorizedException;
import com.example.musicai.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongMapper songMapper;

    @Value("${file.upload.audio-dir}")
    private String audioDir;

    @Value("${file.upload.cover-dir}")
    private String coverDir;

    @Transactional
    public SongResponse uploadSong(MultipartFile audio, MultipartFile cover, String title, String artist, String album, Long uploaderId) throws IOException {
        // 验证必需字段
        if (audio == null || audio.isEmpty()) {
            throw new IllegalArgumentException("音频文件不能为空");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("歌曲标题不能为空");
        }
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("艺术家不能为空");
        }

        // 验证音频文件格式
        String audioFileName = audio.getOriginalFilename();
        if (audioFileName == null || (!audioFileName.toLowerCase().endsWith(".mp3") && !audioFileName.toLowerCase().endsWith(".flac"))) {
            throw new IllegalArgumentException("音频文件格式不支持，仅支持mp3和flac格式");
        }

        // 创建存储目录
        File audioDirFile = new File(audioDir);
        if (!audioDirFile.exists()) {
            audioDirFile.mkdirs();
        }
        File coverDirFile = new File(coverDir);
        if (!coverDirFile.exists()) {
            coverDirFile.mkdirs();
        }

        // 保存音频文件
        String audioFileExtension = audioFileName.substring(audioFileName.lastIndexOf('.'));
        String audioFileNameNew = UUID.randomUUID().toString() + audioFileExtension;
        Path audioPath = Paths.get(audioDir, audioFileNameNew);
        Files.write(audioPath, audio.getBytes());

        // 保存封面图片（如果提供）
        String coverPath = null;
        if (cover != null && !cover.isEmpty()) {
            String coverFileName = cover.getOriginalFilename();
            if (coverFileName != null && (coverFileName.toLowerCase().endsWith(".jpg") || coverFileName.toLowerCase().endsWith(".jpeg") || coverFileName.toLowerCase().endsWith(".png"))) {
                String coverFileExtension = coverFileName.substring(coverFileName.lastIndexOf('.'));
                String coverFileNameNew = UUID.randomUUID().toString() + coverFileExtension;
                Path coverFilePath = Paths.get(coverDir, coverFileNameNew);
                Files.write(coverFilePath, cover.getBytes());
                coverPath = coverFileNameNew;
            }
        }

        // 创建歌曲记录
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setAlbum(album);
        song.setFilePath(audioFileNameNew);
        song.setCoverPath(coverPath);
        song.setUploaderId(uploaderId);
        song.setPlayCount(0);
        song.setDuration(0); // 可以从音频文件解析，这里简化处理
        song.setCreatedAt(LocalDateTime.now());

        songMapper.insert(song);

        return convertToResponse(song);
    }

    public PageResponse<SongResponse> getSongs(int page, int limit, String search) {
        int offset = (page - 1) * limit;
        List<Song> songs = songMapper.findAll(offset, limit, search);
        long total = songMapper.count(search);

        List<SongResponse> songResponses = songs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(songResponses, page, limit, total);
    }

    public SongResponse getSongById(Long id) {
        Song song = songMapper.findById(id);
        if (song == null) {
            throw new ResourceNotFoundException("歌曲不存在");
        }
        return convertToResponse(song);
    }

    @Transactional
    public SongResponse updateSong(Long id, SongUpdateRequest request, Long userId) {
        Song song = songMapper.findById(id);
        if (song == null) {
            throw new ResourceNotFoundException("歌曲不存在");
        }

        // 检查权限：只有上传者可以更新
        if (!song.getUploaderId().equals(userId)) {
            throw new UnauthorizedException("没有权限修改此歌曲");
        }

        if (request.getTitle() != null) {
            song.setTitle(request.getTitle());
        }
        if (request.getArtist() != null) {
            song.setArtist(request.getArtist());
        }
        if (request.getAlbum() != null) {
            song.setAlbum(request.getAlbum());
        }

        songMapper.update(song);
        return convertToResponse(song);
    }

    @Transactional
    public void deleteSong(Long id, Long userId) {
        Song song = songMapper.findById(id);
        if (song == null) {
            throw new ResourceNotFoundException("歌曲不存在");
        }

        // 检查权限：只有上传者可以删除
        if (!song.getUploaderId().equals(userId)) {
            throw new UnauthorizedException("没有权限删除此歌曲");
        }

        // 删除文件
        try {
            if (song.getFilePath() != null) {
                Path audioPath = Paths.get(audioDir, song.getFilePath());
                Files.deleteIfExists(audioPath);
            }
            if (song.getCoverPath() != null) {
                Path coverPath = Paths.get(coverDir, song.getCoverPath());
                Files.deleteIfExists(coverPath);
            }
        } catch (IOException e) {
            // 记录日志，但不阻止删除操作
            e.printStackTrace();
        }

        songMapper.deleteById(id);
    }

    public Song getSongEntity(Long id) {
        Song song = songMapper.findById(id);
        if (song == null) {
            throw new ResourceNotFoundException("歌曲不存在");
        }
        return song;
    }

    private SongResponse convertToResponse(Song song) {
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
    }
}


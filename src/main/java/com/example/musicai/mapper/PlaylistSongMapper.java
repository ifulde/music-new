package com.example.musicai.mapper;

import com.example.musicai.entity.PlaylistSong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistSongMapper {
    int insert(PlaylistSong playlistSong);
    List<Long> findSongIdsByPlaylistId(Long playlistId);
    int deleteByPlaylistIdAndSongId(@Param("playlistId") Long playlistId, @Param("songId") Long songId);
    int deleteByPlaylistId(Long playlistId);
    PlaylistSong findByPlaylistIdAndSongId(@Param("playlistId") Long playlistId, @Param("songId") Long songId);
}


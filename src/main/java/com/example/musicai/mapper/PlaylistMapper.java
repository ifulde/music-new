package com.example.musicai.mapper;

import com.example.musicai.entity.Playlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistMapper {
    int insert(Playlist playlist);
    Playlist findById(Long id);
    List<Playlist> findByUserId(Long userId);
    int update(Playlist playlist);
    int deleteById(Long id);
}


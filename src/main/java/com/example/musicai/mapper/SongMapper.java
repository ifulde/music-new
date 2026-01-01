package com.example.musicai.mapper;

import com.example.musicai.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SongMapper {
    int insert(Song song);
    Song findById(Long id);
    List<Song> findAll(@Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);
    long count(@Param("search") String search);
    int update(Song song);
    int deleteById(Long id);
    int incrementPlayCount(Long id);
}


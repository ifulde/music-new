package com.example.musicai.mapper;

import com.example.musicai.entity.PlayHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayHistoryMapper {
    int insert(PlayHistory playHistory);
    List<PlayHistory> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    long countByUserId(Long userId);
}


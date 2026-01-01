package com.example.musicai.mapper;

import com.example.musicai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User user);
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
}


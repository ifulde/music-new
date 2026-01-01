package com.example.musicai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.musicai.mapper")
public class MusicAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicAiApplication.class, args);
    }

}

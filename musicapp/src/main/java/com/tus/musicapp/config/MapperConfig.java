package com.tus.musicapp.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tus.musicapp.mapper.SongMapper;

@Configuration
public class MapperConfig {
    @Bean
    public SongMapper songMapper() {
        return Mappers.getMapper(SongMapper.class);
    }
}

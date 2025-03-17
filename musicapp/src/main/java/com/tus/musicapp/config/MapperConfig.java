package com.tus.musicapp.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tus.musicapp.mapper.SongMapper;

 // Configuration class responsible for defining MapStruct mappers as Spring beans
 // Ensures that MapStruct-generated mappers can be injected into services and controllers where needed
@Configuration
public class MapperConfig {
    @Bean
    public SongMapper songMapper() {
        return Mappers.getMapper(SongMapper.class);
    }
}

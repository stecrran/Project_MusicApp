package com.tus.musicapp.mapper;

import org.mapstruct.Mapper;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Song;

@Mapper(componentModel = "spring")
public interface SongMapper {

    SongDto toDto(Song song);

    Song toEntity(MusicCreationDto musicCreationDto);
}

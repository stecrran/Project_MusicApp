package com.tus.musicapp.mapper;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Song;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-14T18:49:54+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class SongMapperImpl implements SongMapper {

    @Override
    public SongDto toDto(Song song) {
        if ( song == null ) {
            return null;
        }

        SongDto songDto = new SongDto();

        songDto.setAlbum( song.getAlbum() );
        songDto.setArtist( song.getArtist() );
        songDto.setDurationMs( song.getDurationMs() );
        songDto.setGenre( song.getGenre() );
        songDto.setId( song.getId() );
        songDto.setSpotifyId( song.getSpotifyId() );
        songDto.setSpotifyUrl( song.getSpotifyUrl() );
        songDto.setTitle( song.getTitle() );

        return songDto;
    }

    @Override
    public Song toEntity(MusicCreationDto musicCreationDto) {
        if ( musicCreationDto == null ) {
            return null;
        }

        Song song = new Song();

        song.setSpotifyId( musicCreationDto.getSpotifyId() );
        song.setTitle( musicCreationDto.getTitle() );
        song.setArtist( musicCreationDto.getArtist() );
        song.setAlbum( musicCreationDto.getAlbum() );
        song.setGenre( musicCreationDto.getGenre() );
        if ( musicCreationDto.getDurationMs() != null ) {
            song.setDurationMs( musicCreationDto.getDurationMs() );
        }
        song.setSpotifyUrl( musicCreationDto.getSpotifyUrl() );

        return song;
    }
}

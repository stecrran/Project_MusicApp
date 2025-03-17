package com.tus.musicapp.mapper;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Song;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-17T16:07:26+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.41.0.v20250213-1140, environment: Java 22.0.1 (Eclipse Adoptium)"
)
@Component
public class SongMapperImpl implements SongMapper {

    @Override
    public SongDto toDto(Song song) {
        if ( song == null ) {
            return null;
        }

        SongDto songDto = new SongDto();

        songDto.setUsers( mapUsersToUsernames( song.getUsers() ) );
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

        song.setAlbum( musicCreationDto.getAlbum() );
        song.setArtist( musicCreationDto.getArtist() );
        if ( musicCreationDto.getDurationMs() != null ) {
            song.setDurationMs( musicCreationDto.getDurationMs() );
        }
        song.setGenre( musicCreationDto.getGenre() );
        song.setSpotifyId( musicCreationDto.getSpotifyId() );
        song.setSpotifyUrl( musicCreationDto.getSpotifyUrl() );
        song.setTitle( musicCreationDto.getTitle() );

        return song;
    }
}

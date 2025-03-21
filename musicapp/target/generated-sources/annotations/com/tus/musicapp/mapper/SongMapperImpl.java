package com.tus.musicapp.mapper;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Song;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-21T03:27:07+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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
        songDto.setId( song.getId() );
        songDto.setSpotifyId( song.getSpotifyId() );
        songDto.setTitle( song.getTitle() );
        songDto.setArtist( song.getArtist() );
        songDto.setAlbum( song.getAlbum() );
        songDto.setGenre( song.getGenre() );
        songDto.setDurationMs( song.getDurationMs() );
        songDto.setSpotifyUrl( song.getSpotifyUrl() );

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

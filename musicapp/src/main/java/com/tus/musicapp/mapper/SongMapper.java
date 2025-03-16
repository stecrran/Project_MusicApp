package com.tus.musicapp.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.tus.musicapp.dto.MusicCreationDto;
import com.tus.musicapp.dto.SongDto;
import com.tus.musicapp.model.Song;
import com.tus.musicapp.model.User;

@Mapper(componentModel = "spring")
public interface SongMapper {

	@Mapping(source = "users", target = "users", qualifiedByName = "mapUsersToUsernames")
    SongDto toDto(Song song);

    Song toEntity(MusicCreationDto musicCreationDto);
    
    // ✅ Custom mapping method to convert Set<User> to List<String> (usernames)
    @Named("mapUsersToUsernames")
    default List<String> mapUsersToUsernames(Set<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}

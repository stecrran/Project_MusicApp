package com.tus.musicapp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tus.musicapp.model.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

}

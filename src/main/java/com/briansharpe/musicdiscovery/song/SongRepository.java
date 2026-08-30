package com.briansharpe.musicdiscovery.song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
    boolean existsByTitleIgnoreCaseAndArtistId(String title, Long artistId);
}

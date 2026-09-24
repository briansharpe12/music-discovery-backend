package com.briansharpe.musicdiscovery.playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);
}
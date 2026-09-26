package com.briansharpe.musicdiscovery.playlistsong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    List<PlaylistSong> findByPlaylistIdOrderByPositionAsc(Long playlistId);
    boolean existsByPlaylistIdAndSongId(Long playlistId, Long songId);
}
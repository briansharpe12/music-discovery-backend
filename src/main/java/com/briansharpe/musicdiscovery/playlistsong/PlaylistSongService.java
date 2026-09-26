package com.briansharpe.musicdiscovery.playlistsong;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.playlist.Playlist;
import com.briansharpe.musicdiscovery.playlist.PlaylistRepository;
import com.briansharpe.musicdiscovery.song.Song;
import com.briansharpe.musicdiscovery.song.SongRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaylistSongService {

    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    public PlaylistSongService(PlaylistSongRepository playlistSongRepository, PlaylistRepository playlistRepository, SongRepository songRepository) {
        this.playlistSongRepository = playlistSongRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    @Transactional
    public PlaylistSong addSongToPlaylist(Long playlistId, Long songId, Integer position) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(()
                -> new ResourceNotFoundException("Playlist not found with provided id: " + playlistId));
        Song song = songRepository.findById(songId).orElseThrow(()
                -> new ResourceNotFoundException("Song not found with provided id: " + songId));
        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId)) {
                throw new IllegalArgumentException("This Song is already added to this playlist");
        }
        PlaylistSong playlistSong = new PlaylistSong(playlist, song, position);
        return playlistSongRepository.save(playlistSong);
    }


    public PlaylistSong getPlaylistSong(Long playlistId, Long songId) {
        PlaylistSongId id = new PlaylistSongId(playlistId, songId);
        return playlistSongRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Song with id " + songId + " was not found in playlist with id " + playlistId));
    }

    public List<PlaylistSong> getSongsByPlaylistId(Long playlistId) {
        playlistRepository.findById(playlistId).orElseThrow(()
                -> new ResourceNotFoundException("Playlist not found with provided id: " + playlistId));
        return playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
    }

    @Transactional
    public PlaylistSong updateSongPosition(Long playlistId, Long songId, Integer position) {
        PlaylistSong playlistSong = getPlaylistSong(playlistId, songId);
        playlistSong.setPosition(position);
        return playlistSongRepository.save(playlistSong);
    }

    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        PlaylistSong playlistSong = getPlaylistSong(playlistId, songId);
        playlistSongRepository.delete(playlistSong);
    }
}
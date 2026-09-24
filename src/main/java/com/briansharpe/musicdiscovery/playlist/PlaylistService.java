package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.user.User;
import com.briansharpe.musicdiscovery.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    public PlaylistService(PlaylistRepository playlistRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
    }

    public Playlist createPlaylist(String name, String description, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No user found with provided id: " + userId));
        Playlist createdPlaylist = new Playlist(name, description, owner);
        return playlistRepository.save(createdPlaylist);
    }

    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No playlist found with provided id: " + id));
    }

    public List<Playlist> getPlaylistsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No user found with provided id: " + userId));
        return playlistRepository.findByUserId(userId);
    }

    public Playlist updatePlaylist(Long id, String name, String description) {
        Playlist updatingPlaylist = playlistRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found with provided id: " + id));
        updatingPlaylist.setName(name);
        updatingPlaylist.setDescription(description);
        return playlistRepository.save(updatingPlaylist);
    }

    public void deletePlaylist(Long id) {
        Playlist deletingPlaylist = playlistRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Playlist not found with provided id: " + id));
        playlistRepository.delete(deletingPlaylist);
    }
}
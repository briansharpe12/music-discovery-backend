package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.playlist.dto.CreatePlaylistRequest;
import com.briansharpe.musicdiscovery.playlist.dto.PlaylistResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;
    public PlaylistController(PlaylistService playlistService) {this.playlistService = playlistService;}

    @PostMapping("/user/{userId}")
    public ResponseEntity<PlaylistResponse> createPlaylist(@PathVariable Long userId, @Valid @RequestBody CreatePlaylistRequest request) {
        Playlist returnedPlaylist = playlistService.createPlaylist(request.name(), request.description(), userId);
        PlaylistResponse responseEntity = new PlaylistResponse(
                returnedPlaylist.getId(),
                returnedPlaylist.getName(),
                returnedPlaylist.getDescription(),
                returnedPlaylist.getUser().getId(),
                returnedPlaylist.getUser().getUsername(),
                returnedPlaylist.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long id) {
        Playlist returnedPlaylist = playlistService.getPlaylistById(id);
        PlaylistResponse responseEntity = new PlaylistResponse(
                returnedPlaylist.getId(),
                returnedPlaylist.getName(),
                returnedPlaylist.getDescription(),
                returnedPlaylist.getUser().getId(),
                returnedPlaylist.getUser().getUsername(),
                returnedPlaylist.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistResponse>> getPlaylistsByUserId(@PathVariable Long userId) {
        List<Playlist> returnedPlaylists = playlistService.getPlaylistsByUserId(userId);
        List<PlaylistResponse> responseEntitiesList = new ArrayList<>(returnedPlaylists.size());
        for (Playlist playlist : returnedPlaylists) {
            responseEntitiesList.add( new PlaylistResponse(
                    playlist.getId(),
                    playlist.getName(),
                    playlist.getDescription(),
                    playlist.getUser().getId(),
                    playlist.getUser().getUsername(),
                    playlist.getCreatedAt()
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseEntitiesList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(@PathVariable Long id, @Valid @RequestBody CreatePlaylistRequest request) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, request.name(), request.description());
        PlaylistResponse responseEntity = new PlaylistResponse(
                updatedPlaylist.getId(),
                updatedPlaylist.getName(),
                updatedPlaylist.getDescription(),
                updatedPlaylist.getUser().getId(),
                updatedPlaylist.getUser().getUsername(),
                updatedPlaylist.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
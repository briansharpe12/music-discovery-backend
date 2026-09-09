package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.song.dto.CreateSongRequest;
import com.briansharpe.musicdiscovery.song.dto.SongResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    private final  SongService songService;
    public SongController(SongService songService) {this.songService = songService;}

    @PostMapping
    public ResponseEntity<SongResponse> createSong(@Valid @RequestBody CreateSongRequest req) {
        String songTitle = req.title();
        Integer songDuration = req.durationSeconds();
        Long songArtistId = req.artistId();
        Song returnedSong = songService.createSong(songTitle, songDuration, songArtistId);
        SongResponse songResponse = new SongResponse(returnedSong.getId(), returnedSong.getTitle(), returnedSong.getDurationSeconds(),
                returnedSong.getArtist().getId(), returnedSong.getArtist().getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(songResponse);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongByID(@PathVariable Long id){
        Song returnedSong =  songService.getSongById(id);
        SongResponse SongEntity = new SongResponse(returnedSong.getId(), returnedSong.getTitle(),
                returnedSong.getDurationSeconds(),returnedSong.getArtist().getId(),returnedSong.getArtist().getName());
        return ResponseEntity.status(HttpStatus.OK).body(SongEntity);

    }

    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        List<Song> returnedSongs = songService.getAllSongs();
        List<SongResponse> SongEntities = new ArrayList<>(returnedSongs.size());
        for (Song song : returnedSongs) {
            SongEntities.add(new SongResponse(song.getId(), song.getTitle(),
                    song.getDurationSeconds(), song.getArtist().getId(),song.getArtist().getName()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(SongEntities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongResponse> updateSong(@PathVariable Long id, @Valid @RequestBody CreateSongRequest req) {
        Song updatedSong =  songService.updateSong(id, req.title(), req.durationSeconds(), req.artistId());
        SongResponse responseEntity = new SongResponse(updatedSong.getId(), updatedSong.getTitle(),  updatedSong.getDurationSeconds(),
                updatedSong.getArtist().getId(),updatedSong.getArtist().getName());
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id){
        songService.deleteSongById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

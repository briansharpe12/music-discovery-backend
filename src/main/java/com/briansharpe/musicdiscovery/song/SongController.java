package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.song.dto.CreateSongRequest;
import com.briansharpe.musicdiscovery.song.dto.SongResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


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

}

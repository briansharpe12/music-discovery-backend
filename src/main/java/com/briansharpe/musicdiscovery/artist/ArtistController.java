package com.briansharpe.musicdiscovery.artist;
import com.briansharpe.musicdiscovery.artist.dto.ArtistResponse;
import com.briansharpe.musicdiscovery.artist.dto.CreateArtistRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    private final ArtistService artistService;
    public ArtistController(ArtistService artistService) {this.artistService = artistService;}

    @PostMapping
    public ResponseEntity<ArtistResponse> createArtist(@Valid @RequestBody CreateArtistRequest request) {
        String name = request.name();
        Artist returnedArtist = artistService.createArtist(name);
        ArtistResponse responseEntity = new ArtistResponse (returnedArtist.getId(), returnedArtist.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtist(@PathVariable Long id) {
        Artist returnedArtist = artistService.getArtistById(id);
        ArtistResponse responseEntity = new ArtistResponse (returnedArtist.getId(), returnedArtist.getName());
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
        List<Artist> listOfArtists = artistService.getAllArtists();
        List<ArtistResponse> responseEntities = new ArrayList<>(listOfArtists.size());
        for (Artist artist : listOfArtists) {
            responseEntities.add(new ArtistResponse(artist.getId(), artist.getName()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseEntities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(@PathVariable Long id, @Valid @RequestBody CreateArtistRequest request) {
        String name = request.name();
        Artist returnedArtist = artistService.updateArtist(id, name);
        ArtistResponse responseEntity = new ArtistResponse (returnedArtist.getId(), returnedArtist.getName());
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
package com.briansharpe.musicdiscovery.artist;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist createArtist(String name) {
        if (artistRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Artist already exists");
        }
        Artist newlyCreatedArtist = new Artist(name);
        return artistRepository.save(newlyCreatedArtist);
        }

}
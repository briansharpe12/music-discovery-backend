package com.briansharpe.musicdiscovery.artist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Artist getArtistById(Long id) {
       return artistRepository.findById(id).orElseThrow(()
               -> new ResourceNotFoundException("Artist not found with provided id: " + id));
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Artist updateArtist(Long id, String name){
        Artist updatingArtist = artistRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Artist not found with provided id: " + id));

        updatingArtist.setName(name);
        return artistRepository.save(updatingArtist);
    }

    public void deleteArtist(Long id){
        artistRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Artist not found with provided id: " + id));
        artistRepository.deleteById(id);
    }



}
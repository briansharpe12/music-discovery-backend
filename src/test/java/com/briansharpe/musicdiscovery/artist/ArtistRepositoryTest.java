package com.briansharpe.musicdiscovery.artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArtistRepositoryTest {


    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void saveAndRetrieveArtist() {
        Artist artist = new Artist("Kanye West");
        Artist savedArtist = artistRepository.save(artist);
        assertNotNull(savedArtist.getId());
        Artist retrievedArtist = artistRepository.findById(savedArtist.getId()).orElseThrow();
        assertEquals("Kanye West", retrievedArtist.getName());
        //delete test object post execution
        artistRepository.deleteById(savedArtist.getId());
    }

}

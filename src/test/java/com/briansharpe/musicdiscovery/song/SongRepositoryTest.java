package com.briansharpe.musicdiscovery.song;

import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.artist.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SongRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void toSaveAndRetrieveSongWithArtist() {
        //parent first then child that references parent
        Artist songsArtist = new Artist("The Hip Abduction");
        Artist savedSongsArtist = artistRepository.save(songsArtist);

        Song songChoice = new Song("Chasin Waves", 200, savedSongsArtist);
        Song savedSongChoice = songRepository.save(songChoice);

        //Verifying ID generated in database then read song back from database
        assertNotNull(savedSongChoice.getId());
        Song retrievedSong = songRepository.findById(savedSongChoice.getId()).orElseThrow();

        //Verify song parameters
        assertEquals("Chasin Waves", retrievedSong.getTitle());
        assertEquals(200,retrievedSong.getDurationSeconds());

        //Verify parent - child relationship was persisted
        assertNotNull(retrievedSong.getArtist());
        assertEquals(savedSongsArtist.getId(), retrievedSong.getArtist().getId());
        assertEquals("The Hip Abduction", retrievedSong.getArtist().getName());

        //deleting backwards - child then parent
        songRepository.deleteById(savedSongChoice.getId());
        artistRepository.deleteById(savedSongsArtist.getId());

    }

}
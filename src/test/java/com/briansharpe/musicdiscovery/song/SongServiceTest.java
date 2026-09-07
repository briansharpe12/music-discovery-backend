package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.artist.ArtistRepository;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongServiceTest {

    @Mock
    private SongRepository songRepository;
    @Mock
    private ArtistRepository artistRepository;

    private SongService songService;

    @BeforeEach
    void setUp() { songService = new SongService(songRepository, artistRepository); }

    @Test
    void shouldCreateSong() {
        // Arrange Artist + Song Repo setup
        Artist artist = new Artist("BrianSharpe");
        when(artistRepository.findById(10L)).thenReturn(Optional.of(artist));
        when(songRepository.existsByTitleIgnoreCaseAndArtistId("Test Song", 10L)).thenReturn(false);
        Song song = new Song("Test Song",180, artist);
        when(songRepository.save(any(Song.class))).thenReturn(song);

        //Act
        Song testResult = songService.createSong("Test Song", 180,10L);

        //Assert
        assertEquals("Test Song",testResult.getTitle());
        assertEquals(180,testResult.getDurationSeconds());
        assertEquals("BrianSharpe", testResult.getArtist().getName());

        //Verify Proper Service + Repo interaction occurred
        verify(artistRepository).findById(10L);
        verify(songRepository).existsByTitleIgnoreCaseAndArtistId("Test Song", 10L);
        verify(songRepository).save(any(Song.class));
    }


    @Test
    void shouldRejectDuplicateSong() {
        // Arrange Artist + Song Repo setup
        Artist artist = new Artist("BrianSharpe");
        when(artistRepository.findById(10L)).thenReturn(Optional.of(artist));
        when(songRepository.existsByTitleIgnoreCaseAndArtistId("Test Song", 10L)).thenReturn(true);
        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {songService.createSong("Test Song", 180,10L);});
        //Verify Proper Service + Repo interaction occurred
        verify(artistRepository).findById(10L);
        verify(songRepository).existsByTitleIgnoreCaseAndArtistId("Test Song", 10L);
        verify(songRepository,never()).save(any(Song.class));
    }

    @Test
    void shouldThrowWhenCreatingSongWithMissingArtist() {
        when(artistRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {songService.createSong("Test Song", 180,10L);});
        verify(artistRepository).findById(10L);
    }

    @Test
    void shouldGetSongById() {
        // Arrange
        Artist artist = new Artist("BrianSharpe");
        Song song = new Song("Test Song", 180, artist);
        when(songRepository.findById(10L)).thenReturn(Optional.of(song));

        // Act
        Song testResult = songService.getSongById(10L);

        // Assert
        assertEquals("Test Song", testResult.getTitle());
        assertEquals(180, testResult.getDurationSeconds());

        // Verify successful interaction between serv + repo
        verify(songRepository).findById(10L);
    }

    @Test
    void shouldThrowWhenSongIdNotFound() {
        // Arrange
        when(songRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {songService.getSongById(10L);});
        verify(songRepository).findById(10L);
    }

    @Test
    void shouldGetAllSongs() {
        //Arrange
        Song song1 = new Song("TestSong1", 180, new Artist("Brian"));
        Song song2  = new Song("TestSong2", 150, new Artist("Sharpe"));
        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);
        when(songRepository.findAll()).thenReturn(songList);

        //Act
        List <Song> result = songService.getAllSongs();

        //Assert
        assertEquals(songList, result);

        //Verify
        verify(songRepository).findAll();

    }


    @Test
    void shouldUpdateSong() {
        //Arrange
        Artist artist = new Artist("Brian");
        Song testSong = new Song("TestSong1", 180, artist);
        when(artistRepository.findById(2L)).thenReturn(Optional.of(artist));
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        //Act
        Song testResult = songService.updateSong(1L,"UpdatedTestSongTitle",180,2L);

        //Assert
        assertEquals("UpdatedTestSongTitle",testResult.getTitle());
        assertEquals(180,testResult.getDurationSeconds());
        assertEquals("Brian", testResult.getArtist().getName());

        //Verify
        verify(songRepository).findById(1L);
        verify(artistRepository).findById(2L);
        verify(songRepository).save(testSong);
    }

    @Test
    void shouldThrowWhenUpdatingMissingSong() {
        //Arrange
        when(songRepository.findById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> songService.updateSong(1L,"MissingSongTitle",180,2L));

        //Verify Proper Workflow occurred: FindbyID for song only + throw immedately + No Save
        verify(songRepository).findById(1L);
        verify(artistRepository, never()).findById(anyLong());
        verify(songRepository, never()).save(any(Song.class));
    }

    @Test
    void shouldThrowWhenUpdatingSongWithMissingArtist() {
        //Arrange
        when(songRepository.findById(1L)).thenReturn(Optional.of(new Song()));
        when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> songService.updateSong(1L,"MissingArtistTitle",180,2L));

        //Verify
        verify(songRepository).findById(1L);
        verify(artistRepository).findById(2L);
        verify(songRepository, never()).save(any(Song.class));
    }

    @Test
    void shouldDeleteSong() {
        //Arrange
        Artist artist = new Artist("TestArtist");
        Song song = new Song("TestSong", 180, artist);

        //Act
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        //Assert
        songService.deleteSongById(1L);
        //Verify
        verify(songRepository).findById(1L);
        verify(songRepository).delete(song);
    }

    @Test
    void shouldThrowWhenDeletingMissingSong() {
        //Arrange
        when(songRepository.findById(1L)).thenReturn(Optional.empty());
        //Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> songService.deleteSongById(1L));
        //Verify
        verify(songRepository).findById(1L);
        verify(songRepository, never()).delete(any(Song.class));
    }


}
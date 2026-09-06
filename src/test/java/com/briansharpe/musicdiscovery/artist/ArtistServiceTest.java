package com.briansharpe.musicdiscovery.artist;

import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {


    @Mock
    private ArtistRepository artistRepository;

    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        artistService = new ArtistService(artistRepository);
    }

    @Test
    void shouldCreateArtist() {
        //Arrange   -- Setup proper scenario
        when(artistRepository.existsByNameIgnoreCase("The Weeknd")).thenReturn(false);
        Artist savedArtist = new Artist("The Weeknd");
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        //Act       -- Call method we are testing:
        Artist result = artistService.createArtist("The Weeknd");

        //Assert    -- inspect result via JUNIT assertions with expected string vs result's string
        assertEquals("The Weeknd", result.getName());

        //Verify    -- Inspect repository interactions
        verify(artistRepository).existsByNameIgnoreCase("The Weeknd");
        verify(artistRepository).save(any(Artist.class));

    }

    @Test
    void shouldRejectDuplicateArtist() {

        // Arrange              -- Pretend "The Weeknd" already exists
        when(artistRepository.existsByNameIgnoreCase("The Weeknd")).thenReturn(true);

        //Act  + Assert         -- Creating the weeknd we would expect an ill arg exception
        assertThrows(IllegalArgumentException.class, () -> artistService.createArtist("The Weeknd"));

        // Verify               -- Confirm duplicate check happened
        verify(artistRepository).existsByNameIgnoreCase("The Weeknd");
        //  Verify              -- Confirm save() did NOT happen
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    void shouldGetArtistById() {
        //Arrange               -- Setup proper scenario
        Artist artist = new Artist("Brian");
        when(artistRepository.findById(100L)).thenReturn(Optional.of(artist));

        // Act
        Artist result = artistService.getArtistById(100L);

        //Assert                -- Result mapped correctly to Brian
        assertEquals("Brian", result.getName());

        //Verify
        verify(artistRepository).findById(100L);
    }

    @Test
    void shouldThrowWhenArtistIdNotFound(){
        //Arrange    -- This scenario the ID cannot be found
        when(artistRepository.findById(100L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> artistService.getArtistById(100L));

        //Verify                        -- Confirm service calls repository
        verify(artistRepository).findById(100L);
    }

    @Test
    void shouldGetAllArtists(){
        // Arrange
        Artist artist1 = new Artist("Brian");
        Artist artist2 = new Artist("Sharpe");
        List<Artist> artistList = new ArrayList<>();
        artistList.add(artist1);
        artistList.add(artist2);
        when(artistRepository.findAll()).thenReturn(artistList);

        // Act
        List<Artist> result = artistService.getAllArtists();

        // Assert
        assertEquals(artistList, result);
        assertEquals("Brian", result.get(0).getName());
        assertEquals("Sharpe", result.get(1).getName());

        // Verify
        verify(artistRepository).findAll();
    }

    @Test
    void shouldUpdateArtist() {
        // Arrange set up correct scenario
        Artist oldArtist = new Artist("Old Name");
        when(artistRepository.findById(4L)).thenReturn(Optional.of(oldArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(oldArtist);

        // Act              -- Call Method we wish to test
        Artist result = artistService.updateArtist(4L, "New Name");

        // Assert           -- Confirm returned Artist now has "New Name"
        assertEquals("New Name", result.getName());

        // Verify findByID() + save() occurred
        verify(artistRepository).findById(4L);
        verify(artistRepository).save(oldArtist);
    }

    @Test
    void shouldThrowWhenUpdatingMissingArtist() {
        // Arrange
        when(artistRepository.findById(400L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> artistService.updateArtist(400L, "Updated Name"));

        // Verify Proper Workflow occurred FindbyID + No Save
        verify(artistRepository).findById(400L);
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    void shouldDeleteArtist() {
        //Arrange
        Artist removingArtist = new Artist("The Beatles");
        when(artistRepository.findById(10L)).thenReturn(Optional.of(removingArtist));

        // Act
        artistService.deleteArtist(10L);

        //Verify
        verify(artistRepository).findById(10L);
        verify(artistRepository).deleteById(10L);
    }

    @Test
    void shouldThrowWhenDeletingMissingArtist() {
        // Arrange
        when(artistRepository.findById(400L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> artistService.deleteArtist(400L));

        //Verify
        verify(artistRepository).findById(400L);
        verify(artistRepository, never()).deleteById(anyLong());
    }

}
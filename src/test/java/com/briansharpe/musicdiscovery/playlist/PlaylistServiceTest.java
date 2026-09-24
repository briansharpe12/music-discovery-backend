package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.user.User;
import com.briansharpe.musicdiscovery.user.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {playlistService = new PlaylistService( playlistRepository, userRepository);}

    @Test
    void shouldCreatePlaylist() {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));
        Playlist savedPlaylist = new Playlist("Workout", "Gym music", owner);
        when(playlistRepository.save(any(Playlist.class))).thenReturn(savedPlaylist);
        // Act + Assert
        Playlist result = playlistService.createPlaylist("Workout", "Gym music", 10L);
        assertEquals("Workout", result.getName());
        assertEquals("Gym music", result.getDescription());
        assertEquals(owner, result.getUser());
        // Verify
        verify(userRepository).findById(10L);
        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    void shouldThrowWhenCreatingPlaylistForMissingUser() {
        // Arrange
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistService.createPlaylist("Test Playlist", "Test Music", 100L));
        // Verify
        verify(userRepository).findById(100L);
        verify(playlistRepository, never()).save(any(Playlist.class));
    }


     @Test
     void shouldGetPlaylistById() {
        // Arrange
        User owner = new User("PlaylistOwner","playlistowner@email.com");
        Playlist playlist = new Playlist("Sleep","Sleep music", owner);
        when(playlistRepository.findById(5L)).thenReturn(Optional.of(playlist));
        // Act + Assert
        Playlist result = playlistService.getPlaylistById(5L);
        assertEquals("Sleep", result.getName());
        assertEquals("Sleep music", result.getDescription());
        // Verify
        verify(playlistRepository).findById(5L);
    }

    @Test
    void shouldThrowWhenPlaylistIdNotFound() {
        // Arrange
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistService.getPlaylistById(1L));
        // Verify
        verify(playlistRepository).findById(1L);
    }


    @Test
    void shouldGetPlaylistsByUserId() {
        // Arrange
        User owner = new User("PlaylistOwner","playlistowner@email.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));
        Playlist playlist1 = new Playlist("Playlist1", "Description of playlist1", owner);
        Playlist playlist2 = new Playlist("Playlist2","Description of playlist2", owner);
        List<Playlist> playlistList = new ArrayList<>();
        playlistList.add(playlist1);
        playlistList.add(playlist2);
        when(playlistRepository.findByUserId(10L)).thenReturn(playlistList);
        // Act + Assert
        List<Playlist> result = playlistService.getPlaylistsByUserId(10L);
        assertEquals(playlistList, result);
        assertEquals(2, result.size());
        assertEquals("Playlist1", result.get(0).getName());
        assertEquals("Playlist2", result.get(1).getName());
        // Verify
        verify(userRepository).findById(10L);
        verify(playlistRepository).findByUserId(10L);
    }


    @Test
    void shouldThrowWhenGettingPlaylistsForMissingUser() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistService.getPlaylistsByUserId(999L));
        // Verify - Once user does not exist no playlist querying should occur
        verify(userRepository).findById(999L);
        verify(playlistRepository, never()).findByUserId(anyLong());
    }

    @Test
    void shouldUpdatePlaylist() {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        Playlist existingPlaylist = new Playlist("Old Playlist Name", "Old description", owner);
        when(playlistRepository.findById(5L)).thenReturn(Optional.of(existingPlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(existingPlaylist);
        // Act + Assert
        Playlist result = playlistService.updatePlaylist(5L, "New Playlist Name","New Playlist description");
        assertEquals("New Playlist Name", result.getName());
        assertEquals("New Playlist description", result.getDescription());
        // Verify
        verify(playlistRepository).findById(5L);
        verify(playlistRepository).save(existingPlaylist);
    }

    @Test
    void shouldThrowWhenUpdatingMissingPlaylist() {
        // Arrange
        when(playlistRepository.findById(2L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistService.updatePlaylist(2L,"New Playlist Name", "New description"));
        // Verify
        verify(playlistRepository).findById(2L);
        verify(playlistRepository, never()).save(any(Playlist.class));
    }

    @Test
    void shouldDeletePlaylist() {
        // Arrange
        User owner = new User("PlaylistOwner","playlistowner@email.com");
        Playlist deletingPlaylist = new Playlist("Workout","Gym music", owner);
        when(playlistRepository.findById(5L)).thenReturn(Optional.of(deletingPlaylist));
        // Act
        playlistService.deletePlaylist(5L);
        // Verify
        verify(playlistRepository).findById(5L);
        verify(playlistRepository).delete(deletingPlaylist);
    }

    @Test
    void shouldThrowWhenDeletingMissingPlaylist() {
        // Arrange
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistService.deletePlaylist(1L));
        // Verify
        verify(playlistRepository).findById(1L);
        verify(playlistRepository, never()).delete(any(Playlist.class));
    }
}
package com.briansharpe.musicdiscovery.playlistsong;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.playlist.Playlist;
import com.briansharpe.musicdiscovery.playlist.PlaylistRepository;
import com.briansharpe.musicdiscovery.song.Song;
import com.briansharpe.musicdiscovery.song.SongRepository;
import com.briansharpe.musicdiscovery.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistSongServiceTest {

    @Mock
    private PlaylistSongRepository playlistSongRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private SongRepository songRepository;

    private PlaylistSongService playlistSongService;

    @BeforeEach
    void setUp() {playlistSongService = new PlaylistSongService(playlistSongRepository,playlistRepository,songRepository);}


    @Test
    void shouldAddSongToPlaylist() {
        // Arrange: Successful creation of all 3 needed components to perform action
        User owner = new User("TestUser", "testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist", "Test Music",owner);
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song",180,artist);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(songRepository.findById(2L)).thenReturn(Optional.of(song));
        when(playlistSongRepository.existsByPlaylistIdAndSongId(1L, 2L)).thenReturn(false);
        PlaylistSong savedPlaylistSong = new PlaylistSong(playlist, song, 1);
        when(playlistSongRepository.save(any(PlaylistSong.class))).thenReturn(savedPlaylistSong);

        // Act + Assert
        PlaylistSong result = playlistSongService.addSongToPlaylist(1L,2L,1);
        assertEquals(playlist, result.getPlaylist());
        assertEquals(song, result.getSong());
        assertEquals(1, result.getPosition());

        // Verify
        verify(playlistRepository).findById(1L);
        verify(songRepository).findById(2L);
        verify(playlistSongRepository).existsByPlaylistIdAndSongId(1L, 2L);
        verify(playlistSongRepository).save(any(PlaylistSong.class));
    }


    @Test
    void shouldThrowWhenAddingSongToMissingPlaylist() {
        // Arrange
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistSongService.addSongToPlaylist(1L,2L,1));

        // Verify workflow stopped immediately after playlist not found
        verify(playlistRepository).findById(1L);
        verify(songRepository, never()).findById(anyLong());
        verify(playlistSongRepository, never()).existsByPlaylistIdAndSongId(anyLong(),anyLong());
        verify(playlistSongRepository, never()).save(any(PlaylistSong.class));
    }


    @Test
    void shouldThrowWhenAddingMissingSongToPlaylist() {
        // Arrange
        User owner = new User("TestUser","testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist","Test Music",owner);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(songRepository.findById(2L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistSongService.addSongToPlaylist(1L, 2L,1));

        // Verify workflow stopped immediately after song not found
        verify(playlistRepository).findById(1L);
        verify(songRepository).findById(2L);
        verify(playlistSongRepository, never()).existsByPlaylistIdAndSongId(anyLong(),anyLong());
        verify(playlistSongRepository, never()).save(any(PlaylistSong.class));
    }


    @Test
    void shouldRejectDuplicatePlaylistSong() {
        // Arrange
        User owner = new User("TestUser", "testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist", "Test Music", owner);
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song",180,artist);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(songRepository.findById(2L)).thenReturn(Optional.of(song));
        when(playlistSongRepository.existsByPlaylistIdAndSongId(1L, 2L)).thenReturn(true);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> playlistSongService.addSongToPlaylist(1L, 2L,1));

        // Verify workflow stopped immediately after song + playlist combo already found present
        verify(playlistRepository).findById(1L);
        verify(songRepository).findById(2L);
        verify(playlistSongRepository).existsByPlaylistIdAndSongId(1L, 2L);
        verify(playlistSongRepository, never()).save(any(PlaylistSong.class));
    }


    @Test
    void shouldGetPlaylistSong() {
        // Arrange
        User owner = new User("TestUser", "testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist", "Test Music", owner);
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song", 180, artist);
        PlaylistSong playlistSong = new PlaylistSong(playlist, song, 1);
        PlaylistSongId expectedId = new PlaylistSongId(1L, 2L);
        when(playlistSongRepository.findById(expectedId)).thenReturn(Optional.of(playlistSong));

        // Act
        PlaylistSong result = playlistSongService.getPlaylistSong(1L,2L);

        // Assert
        assertEquals(playlistSong, result);

        // Verify
        verify(playlistSongRepository).findById(expectedId);
    }


    @Test
    void shouldThrowWhenPlaylistSongNotFound() {
        // Arrange
        PlaylistSongId expectedId = new PlaylistSongId(1L, 2L);
        when(playlistSongRepository.findById(expectedId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistSongService.getPlaylistSong(1L, 2L));

        // Verify
        verify(playlistSongRepository).findById(expectedId);
    }


    @Test
    void shouldGetSongsByPlaylistId() {
        // Arrange
        User owner = new User("TestUser", "testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist", "Test Music", owner);
        Artist artist = new Artist("Test Artist");
        Song song1 = new Song("Song1",180, artist);
        Song song2 = new Song("Song2",200, artist);
        PlaylistSong playlistSong1 = new PlaylistSong(playlist, song1, 1);
        PlaylistSong playlistSong2 = new PlaylistSong(playlist, song2, 2);
        List<PlaylistSong> playlistSongs = new ArrayList<>();
        playlistSongs.add(playlistSong1);
        playlistSongs.add(playlistSong2);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(playlistSongRepository.findByPlaylistIdOrderByPositionAsc(1L)).thenReturn(playlistSongs);

        // Act + Assert
        List<PlaylistSong> result = playlistSongService.getSongsByPlaylistId(1L);
        assertEquals(playlistSongs, result);
        assertEquals(2, result.size());
        assertEquals("Song1", result.get(0).getSong().getTitle());
        assertEquals("Song2", result.get(1).getSong().getTitle());

        // Verify
        verify(playlistRepository).findById(1L);
        verify(playlistSongRepository).findByPlaylistIdOrderByPositionAsc(1L);
    }


    @Test
    void shouldThrowWhenGettingSongsForMissingPlaylist() {
        // Arrange
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> playlistSongService.getSongsByPlaylistId(1L));

        // Verify
        verify(playlistRepository).findById(1L);
        verify(playlistSongRepository, never()).findByPlaylistIdOrderByPositionAsc(anyLong());
    }


    @Test
    void shouldUpdateSongPosition() {
        // Arrange
        User owner = new User("TestUser","testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist","Test Music",owner);
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song",180,artist);
        PlaylistSong playlistSong = new PlaylistSong(playlist, song, 1);
        PlaylistSongId id = new PlaylistSongId(1L, 2L);
        when(playlistSongRepository.findById(id)).thenReturn(Optional.of(playlistSong));
        when(playlistSongRepository.save(playlistSong)).thenReturn(playlistSong);

        // Act + Assert
        PlaylistSong result = playlistSongService.updateSongPosition(1L,2L,3);
        assertEquals(3, result.getPosition());

        // Verify
        verify(playlistSongRepository).findById(id);
        verify(playlistSongRepository).save(playlistSong);
    }


    @Test
    void shouldRemoveSongFromPlaylist() {
        // Arrange
        User owner = new User("TestUser","testuser@email.com");
        Playlist playlist = new Playlist("Test Playlist","Test Music",owner);
        Artist artist = new Artist("Test Artist");
        Song song = new Song("Test Song",180,artist);
        PlaylistSong playlistSong = new PlaylistSong(playlist, song, 8);
        PlaylistSongId id = new PlaylistSongId(1L, 2L);
        when(playlistSongRepository.findById(id)).thenReturn(Optional.of(playlistSong));

        // Act
        playlistSongService.removeSongFromPlaylist(1L,2L);

        // Verify
        verify(playlistSongRepository).findById(id);
        verify(playlistSongRepository).delete(playlistSong);
    }
}

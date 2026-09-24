package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.user.User;
import com.briansharpe.musicdiscovery.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndRetrievePlaylistWithUser() {
        // Parent first:     playlist dependent on user
        User testUser = new User("PlaylistTestUser", "playlisttest@email.com");
        User savedUser = userRepository.save(testUser);
        // Child second:    playlist references saved parent/user
        Playlist testPlaylist = new Playlist("TestPlaylist1","TestDescription", savedUser);
        Playlist savedPlaylist = playlistRepository.save(testPlaylist);
        // Verify generated values
        assertNotNull(savedPlaylist.getId());
        assertNotNull(savedPlaylist.getCreatedAt());
        // Retrieve Playlist from database
        Playlist retrievedPlaylist = playlistRepository.findById(savedPlaylist.getId()).orElseThrow();
        // Verify Playlist fields
        assertEquals("TestPlaylist1",retrievedPlaylist.getName());
        assertEquals("TestDescription",retrievedPlaylist.getDescription());
        // Verify User -> Playlist relationship persisted
        assertNotNull(retrievedPlaylist.getUser());
        assertEquals(savedUser.getId(), retrievedPlaylist.getUser().getId());
        assertEquals("PlaylistTestUser", retrievedPlaylist.getUser().getUsername());
        // Clean up backwards: child first, then parent
        playlistRepository.deleteById(savedPlaylist.getId());
        userRepository.deleteById(savedUser.getId());
    }
    
    @Test
    void shouldFindPlaylistsByUserId() {
        // Arrange users
        User user1 = new User("PlaylistOwner1", "playlistowner1@email.com");
        User user2 = new User("PlaylistOwner2", "playlistowner2@email.com");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        // Arrange playlists
        Playlist playlist1 = new Playlist("Workout", "Workout music",savedUser1);
        Playlist playlist2 = new Playlist("Study", "Study music", savedUser1);
        Playlist playlist3 = new Playlist("Study", "Study music", savedUser2);
        Playlist savedPlaylist1 = playlistRepository.save(playlist1);
        Playlist savedPlaylist2 = playlistRepository.save(playlist2);
        Playlist savedPlaylist3 = playlistRepository.save(playlist3);
        // Act + Assert
        List<Playlist> user1List = playlistRepository.findByUserId(savedUser1.getId());
        assertEquals(2, user1List.size());
        assertTrue(user1List.stream().allMatch(playlist -> playlist.getUser().getId().equals(savedUser1.getId())));
        assertTrue(user1List.stream().anyMatch(playlist -> playlist.getName().equals("Workout")));
        assertTrue(user1List.stream().anyMatch(playlist -> playlist.getName().equals("Study")));
        // Clean up children first then parents
        playlistRepository.deleteById(savedPlaylist1.getId());
        playlistRepository.deleteById(savedPlaylist2.getId());
        playlistRepository.deleteById(savedPlaylist3.getId());
        userRepository.deleteById(savedUser1.getId());
        userRepository.deleteById(savedUser2.getId());
    }
}
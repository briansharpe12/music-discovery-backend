package com.briansharpe.musicdiscovery.playlistsong;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.artist.ArtistRepository;
import com.briansharpe.musicdiscovery.playlist.Playlist;
import com.briansharpe.musicdiscovery.playlist.PlaylistRepository;
import com.briansharpe.musicdiscovery.song.Song;
import com.briansharpe.musicdiscovery.song.SongRepository;
import com.briansharpe.musicdiscovery.user.User;
import com.briansharpe.musicdiscovery.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlaylistSongRepositoryTest {

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;


    @Test
    void saveAndRetrievePlaylistSong() {
        // Arrange:
        // 1st: Parent entities (User, Playlist) followed by their child (User: Playlist) (Artist: Song)
        User user = new User("RepoTestUser", "RepoTestUser1@email.com");
        User savedUser = userRepository.save(user);
        Playlist playlist = new Playlist("Repository Playlist", "Testing PlaylistSong persistence", savedUser);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        Artist artist = new Artist("Repository Test Artist");
        Artist savedArtist = artistRepository.save(artist);
        Song song = new Song("Repository Test Song",200, savedArtist);
        Song savedSong = songRepository.save(song);
        // 2nd: Create relationship after Playlist and Song have database IDs via persistence repo methods above
        PlaylistSong playlistSong = new PlaylistSong(savedPlaylist, savedSong,1);
        PlaylistSong savedPlaylistSong = playlistSongRepository.save(playlistSong);
        // 3rd: Build the composite ID
        PlaylistSongId expectedId = new PlaylistSongId(savedPlaylist.getId(), savedSong.getId());

        // Act
        PlaylistSong retrievedPlaylistSong = playlistSongRepository.findById(expectedId).orElseThrow();

        // Assert:
        // 1st: composite primary key
        assertEquals(savedPlaylist.getId(), retrievedPlaylistSong.getId().getPlaylistId());
        assertEquals(savedSong.getId(), retrievedPlaylistSong.getId().getSongId());
        // 2nd: relationships
        assertEquals(savedPlaylist.getId(), retrievedPlaylistSong.getPlaylist().getId());
        assertEquals(savedSong.getId(), retrievedPlaylistSong.getSong().getId());

        // 3rd: relationship type data (New/unique fields/data created in PlaylistSongs class
        assertEquals(1, retrievedPlaylistSong.getPosition());
        assertNotNull(retrievedPlaylistSong.getAddedAt());

        // Clean up: Do relationships first, then parent entities
        playlistSongRepository.delete(savedPlaylistSong);
        songRepository.deleteById(savedSong.getId());
        artistRepository.deleteById(savedArtist.getId());
        playlistRepository.deleteById(savedPlaylist.getId());
        userRepository.deleteById(savedUser.getId());
    }



    @Test
    void shouldFindPlaylistSongsByPlaylistIdInPositionOrder() {
        // Arrange User + Playlist + Artist + Songs
        User user = new User("RepoTestUser2","RepoTestUser2@email.com");
        User savedUser = userRepository.save(user);
        Playlist playlist1 = new Playlist("Playlist1","First Playlist",savedUser);
        Playlist playlist2 = new Playlist("Playlist2","Second Playlist",savedUser);
        Playlist savedPlaylist1 = playlistRepository.save(playlist1);
        Playlist savedPlaylist2 = playlistRepository.save(playlist2);
        Artist artist = new Artist("Test Artist");
        Artist savedArtist = artistRepository.save(artist);
        Song song1 = new Song("Song1",180,savedArtist);
        Song song2 = new Song("Song2",200,savedArtist);
        Song song3 = new Song("Song3",220,savedArtist);
        Song savedSong1 = songRepository.save(song1);
        Song savedSong2 = songRepository.save(song2);
        Song savedSong3 = songRepository.save(song3);
        // Save Playlist 1 songs (Position Order should be mixed)
        PlaylistSong playlistSong1 = new PlaylistSong(savedPlaylist1,savedSong1,2);
        PlaylistSong playlistSong2 = new PlaylistSong(savedPlaylist1,savedSong2,1);
        // Different playlist saves song 3
        PlaylistSong playlistSong3 = new PlaylistSong(savedPlaylist2,savedSong3,1);
        playlistSongRepository.save(playlistSong1);
        playlistSongRepository.save(playlistSong2);
        playlistSongRepository.save(playlistSong3);

        // Act
        List<PlaylistSong> result = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(savedPlaylist1.getId());

        // Assert
        // 1st:     only Playlist 1 relationships returned 2, ignoring playlistsong3 in palylist2
        assertEquals(2, result.size());
        // 2nd:     repository ordered them by position
        assertEquals(1, result.get(0).getPosition());
        assertEquals(2, result.get(1).getPosition());
        assertEquals("Song2",result.get(0).getSong().getTitle());
        assertEquals("Song1",result.get(1).getSong().getTitle());

        // Verify additional existence query I created (Playlist1 only references songs1 and 2, not song 3)
        assertTrue(playlistSongRepository.existsByPlaylistIdAndSongId(savedPlaylist1.getId(), savedSong1.getId()));
        assertFalse(playlistSongRepository.existsByPlaylistIdAndSongId(savedPlaylist1.getId(), savedSong3.getId()));

        // Clean up relationship rows first
        playlistSongRepository.delete(playlistSong1);
        playlistSongRepository.delete(playlistSong2);
        playlistSongRepository.delete(playlistSong3);

        // Child Before Parent (Song before Artist) + (Playlist before User)
        songRepository.deleteById(savedSong1.getId());
        songRepository.deleteById(savedSong2.getId());
        songRepository.deleteById(savedSong3.getId());
        artistRepository.deleteById(savedArtist.getId());
        playlistRepository.deleteById(savedPlaylist1.getId());
        playlistRepository.deleteById(savedPlaylist2.getId());
        userRepository.deleteById(savedUser.getId());
    }
}
package com.briansharpe.musicdiscovery.song;
import com.briansharpe.musicdiscovery.artist.Artist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.song.dto.CreateSongRequest;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(SongController.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SongService songService;


    @Test
    void shouldCreateSong() throws Exception {
        // Arrange
        Artist artist = new Artist("Test Artist");
        Song savedSong = new Song("Test Song Title", 300, artist);
        when(songService.createSong("Test Song Title", 300, 7L)).thenReturn(savedSong);
        CreateSongRequest clientRequestDTO = new CreateSongRequest("Test Song Title", 300, 7L);
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);


        // Act + Assert
        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.title").value("Test Song Title"))
                        .andExpect(jsonPath("$.durationSeconds").value(300))
                        .andExpect(jsonPath("$.artistName").value("Test Artist"));
        // Verify
        verify(songService).createSong("Test Song Title",300,7L);
    }


    @Test
    void shouldGetSongById() throws Exception {
        // Arrange
        Artist artist = new Artist("Test Artist");
        Song savedSong = new Song("Test Song Title", 300, artist);
        when(songService.getSongById(3L)).thenReturn(savedSong);

        // Act + Assert
        mockMvc.perform(get("/api/songs/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Song Title"))
                .andExpect(jsonPath("$.durationSeconds").value(300))
                .andExpect(jsonPath("$.artistName").value("Test Artist"));


        // Verify
        verify(songService).getSongById(20L);
    }


    @Test
    void shouldGetAllSongs() throws Exception {
        // Arrange
        Artist artist1 = new Artist("TestArtist1");
        Artist artist2 = new Artist("TestArtist2");
        Song song1 = new Song("TestSong1Artist1", 300, artist1);
        Song song2 = new Song("TestSong2Artist2", 300, artist2);
        Song song3 = new Song("TestSong3Artist1", 300, artist1);
        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);
        songList.add(song3);
        when(songService.getAllSongs()).thenReturn(songList);

        // Act + Assert
        mockMvc.perform(get("/api/songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("TestSong1Artist1"))
                .andExpect(jsonPath("$[0].artistName").value("TestArtist1"))
                .andExpect(jsonPath("$[1].title").value("TestSong2Artist2"))
                .andExpect(jsonPath("$[1].artistName").value("TestArtist2"))
                .andExpect(jsonPath("$[2].title").value("TestSong3Artist1"))
                .andExpect(jsonPath("$[2].artistName").value("TestArtist1"));
        // Verify
        verify(songService).getAllSongs();
    }

    @Test
    void ShouldUpdateSong() throws Exception{
        Artist artist = new Artist("Test Artist");
        Song savedSong = new Song("Test Song Title", 300, artist);
        when(songService.updateSong(1000L, "Test Song Title", 300, 7L)).thenReturn(savedSong);
        CreateSongRequest clientRequestDTO =  new CreateSongRequest("Test Song Title", 300, 7L);
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        //Act + Assert
        mockMvc.perform(put("/api/songs/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Song Title"))
                .andExpect(jsonPath("$.durationSeconds").value(300))
                .andExpect(jsonPath("$.artistName").value("Test Artist"));
        //Verify
        verify(songService).updateSong(1000L, "Test Song Title", 300, 7L);
    }


    @Test
    void shouldDeleteSong() throws Exception {
        mockMvc.perform(delete("/api/songs/1000")).andExpect(status().isNoContent());
        verify(songService).deleteSongById(1000L);
    }

    @Test
    void shouldReturnNotFoundWhenSongIdMissing() throws Exception {
        when(songService.getSongById(10L)).thenThrow(new ResourceNotFoundException("Song not found"));
        //Act + Assert
        mockMvc.perform(get("/api/songs/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Song not found"));
        verify(songService).getSongById(10L);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateSong() throws Exception {
        //Arrange
        Artist  artist = new Artist("Test Artist");
        Song savedSong = new Song("Test Song", 300, artist);
        when(songService.createSong("Test Song Duplicated",100,12L)).thenThrow(new IllegalArgumentException("Song already exists"));
        CreateSongRequest  clientRequestDTO =  new CreateSongRequest("Test Song Duplicated", 100, 12L);
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        //Act + Assert + Verify
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Song already exists"));
        verify(songService).createSong("Test Song Duplicated",100,12L);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingSongWithMissingArtist() throws Exception {
        // Arrange
        when(songService.createSong("Test Song", 880, 99L)).thenThrow(new ResourceNotFoundException("Artist not found"));
        CreateSongRequest clientRequestDTO = new CreateSongRequest("Test Song",880,99L);
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        // Act + Assert + Verify
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Artist not found"));
        verify(songService).createSong("Test Song",880, 99L);
    }

    @Test
    void shouldRejectInvalidSongRequest() throws Exception {
        // Arrange
        CreateSongRequest clientRequestDTO =  new CreateSongRequest("",0,null);
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        // Act + Assert + Verify
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(songService);
    }

}
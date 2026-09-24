package com.briansharpe.musicdiscovery.playlist;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import com.briansharpe.musicdiscovery.playlist.dto.CreatePlaylistRequest;
import com.briansharpe.musicdiscovery.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlaylistService playlistService;


    @Test
    void shouldCreatePlaylist() throws Exception {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        Playlist savedPlaylist = new Playlist("Test Playlist","Test Playlist Music", owner);
        when(playlistService.createPlaylist("Test Playlist", "Test Playlist Music", 1L)).thenReturn(savedPlaylist);
        CreatePlaylistRequest clientRequestDTO = new CreatePlaylistRequest("Test Playlist","Test Playlist Music");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/playlists/user/1").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Playlist"))
                .andExpect(jsonPath("$.description").value("Test Playlist Music"))
                .andExpect(jsonPath("$.username").value("PlaylistOwner"));
        // Verify
        verify(playlistService).createPlaylist("Test Playlist","Test Playlist Music",1L);
    }

    @Test
    void shouldGetPlaylistById() throws Exception {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        Playlist playlist = new Playlist("Workout", "Gym music", owner);
        when(playlistService.getPlaylistById(5L)).thenReturn(playlist);
        // Act + Assert
        mockMvc.perform(get("/api/playlists/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Workout"))
                .andExpect(jsonPath("$.description").value("Gym music"))
                .andExpect(jsonPath("$.username").value("PlaylistOwner"));
        // Verify
        verify(playlistService).getPlaylistById(5L);
    }

    @Test
    void shouldGetPlaylistsByUserId() throws Exception {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        Playlist playlist1 = new Playlist("Workout", "Gym music", owner);
        Playlist playlist2 = new Playlist("Study", "Study music", owner);
        List<Playlist> playlistList = new ArrayList<>();
        playlistList.add(playlist1);
        playlistList.add(playlist2);
        when(playlistService.getPlaylistsByUserId(10L)).thenReturn(playlistList);
        // Act + Assert
        mockMvc.perform(get("/api/playlists/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Workout"))
                .andExpect(jsonPath("$[0].description").value("Gym music"))
                .andExpect(jsonPath("$[1].name").value("Study"))
                .andExpect(jsonPath("$[1].description").value("Study music"));
        // Verify
        verify(playlistService).getPlaylistsByUserId(10L);
    }

    @Test
    void shouldUpdatePlaylist() throws Exception {
        // Arrange
        User owner = new User("PlaylistOwner", "playlistowner@email.com");
        Playlist updatedPlaylist = new Playlist("Updated Playlist","Updated description",owner);
        when(playlistService.updatePlaylist(5L, "Updated Playlist","Updated description")).thenReturn(updatedPlaylist);
        CreatePlaylistRequest clientRequestDTO = new CreatePlaylistRequest("Updated Playlist","Updated description");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        // Act + Assert
        mockMvc.perform(put("/api/playlists/5").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Playlist"))
                .andExpect(jsonPath("$.description").value("Updated description"));
        // Verify
        verify(playlistService).updatePlaylist(5L,"Updated Playlist","Updated description");
    }

    @Test
    void shouldDeletePlaylist() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/playlists/5")).andExpect(status().isNoContent());
        // Verify
        verify(playlistService).deletePlaylist(5L);
    }

    @Test
    void shouldReturnNotFoundWhenPlaylistIdMissing() throws Exception {
        // Arrange
        when(playlistService.getPlaylistById(5L)).thenThrow(new ResourceNotFoundException("Playlist not found"));
        // Act + Assert
        mockMvc.perform(get("/api/playlists/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Playlist not found"));
        // Verify
        verify(playlistService).getPlaylistById(5L);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingPlaylistForMissingUser() throws Exception {
        // Arrange
        when(playlistService.createPlaylist("Workout","Gym music",99L)).thenThrow( new ResourceNotFoundException("User not found"));
        CreatePlaylistRequest clientRequestDTO = new CreatePlaylistRequest("Workout","Gym music");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/playlists/user/99").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
        // Verify
        verify(playlistService).createPlaylist("Workout","Gym music",99L);
    }

    @Test
    void shouldReturnNotFoundWhenGettingPlaylistsForMissingUser() throws Exception {
        // Arrange
        when(playlistService.getPlaylistsByUserId(99L)).thenThrow(new ResourceNotFoundException("User not found"));
        // Act + Assert
        mockMvc.perform(get("/api/playlists/user/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
        // Verify
        verify(playlistService).getPlaylistsByUserId(99L);
    }

    @Test
    void shouldRejectInvalidPlaylistRequest() throws Exception {
        // Arrange
        CreatePlaylistRequest clientRequestDTO = new CreatePlaylistRequest("","Description");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/playlists/user/10").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
        // Verify
        verifyNoInteractions(playlistService);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingPlaylist() throws Exception {
        // Arrange
        when(playlistService.updatePlaylist(5L, "Updated Playlist","Updated description")).thenThrow(new ResourceNotFoundException ("Playlist not found"));
        CreatePlaylistRequest clientRequestDTO = new CreatePlaylistRequest("Updated Playlist", "Updated description");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(put("/api/playlists/5").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Playlist not found"));
        // Verify
        verify(playlistService).updatePlaylist(5L,"Updated Playlist","Updated description");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingPlaylist() throws Exception {
        // Arrange
        doThrow( new ResourceNotFoundException("Playlist not found")).when(playlistService).deletePlaylist(5L);
        // Act + Assert
        mockMvc.perform(delete("/api/playlists/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Playlist not found"));
        // Verify
        verify(playlistService).deletePlaylist(5L);
    }
}
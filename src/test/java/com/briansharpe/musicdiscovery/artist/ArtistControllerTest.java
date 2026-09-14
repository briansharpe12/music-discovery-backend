package com.briansharpe.musicdiscovery.artist;
import com.briansharpe.musicdiscovery.artist.dto.CreateArtistRequest;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ArtistController.class)
class ArtistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArtistService artistService;

    @Test
    void shouldCreateArtist() throws Exception {
        // Arrange
        Artist savedArtist = new Artist("Controller Test Artist");
        when(artistService.createArtist("Controller Test Artist")).thenReturn(savedArtist);
        CreateArtistRequest clientRequestDTO = new CreateArtistRequest("Controller Test Artist");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        // Act + Assert: Sim HTTP request to MVC layer - fake post request
        mockMvc.perform(post("/api/artists")
                    //Request body becomes JSON + Insert our created JSON into requests body
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    //Check for correct HTTP status (201)
                    .andExpect(status().isCreated())
                    // Starts at the root JSON object then checks name field for equality
                    .andExpect(jsonPath("$.name").value("Controller Test Artist"));
        // Verify
        verify(artistService).createArtist("Controller Test Artist");
    }

    @Test
    void shouldGetArtistById() throws Exception {
        //Arrange
        Artist savedArtist = new Artist("Controller Test Artist");
        when(artistService.getArtistById(100L)).thenReturn(savedArtist);
        //Act + Assert
        mockMvc.perform(get("/api/artists/100")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Controller Test Artist"));
        //Verify
        verify(artistService).getArtistById(100L);
    }

        @Test
        void shouldGetAllArtists() throws Exception {
            //Arrange
            List<Artist> savedArtistList = new ArrayList<>();
            savedArtistList.add(new Artist("Artist1"));
            savedArtistList.add(new Artist("Artist2"));
            savedArtistList.add(new Artist("Artist3"));
            when(artistService.getAllArtists()).thenReturn(savedArtistList);
            //Act + Assert
            mockMvc.perform(get("/api/artists")).andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Artist1"))
                    .andExpect(jsonPath("$[1].name").value("Artist2"))
                    .andExpect(jsonPath("$[2].name").value("Artist3"));
            //Verify
            verify(artistService).getAllArtists();
        }

    @Test
    void shouldUpdateArtist() throws Exception {
        //Assert
        Artist updateArtist = new Artist("Controller Test Artist");
        when(artistService.updateArtist(100L, "Controller Test Artist")).thenReturn(updateArtist);
        //Mapping client requests from DTOs to JSON
        CreateArtistRequest  clientRequestDTO = new CreateArtistRequest("Controller Test Artist");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        //Act + Assert
        mockMvc.perform(put("/api/artists/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Controller Test Artist"));
        //Verify
        verify(artistService).updateArtist(100L, "Controller Test Artist");
    }

    @Test
    void shouldDeleteArtist() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/artists/100")).andExpect(status().isNoContent());
        // Verify
        verify(artistService).deleteArtist(100L);
    }

    @Test
    void shouldReturnNotFoundWhenArtistIdMissing() throws Exception {
        // Arrange
        when(artistService.getArtistById(100L)).thenThrow(new ResourceNotFoundException("Artist not found"));

        // Act + Assert
        mockMvc.perform(get("/api/artists/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Artist not found"));

        // Verify
        verify(artistService).getArtistById(100L);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateArtist() throws Exception {
        // Arrange
        when(artistService.createArtist("Controller Test Artist")).thenThrow(new IllegalArgumentException("Artist already exists"));
        CreateArtistRequest clientRequestDTO =  new CreateArtistRequest("Controller Test Artist");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/artists")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(requestJson))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.error").value("Artist already exists"));
        // Verify
        verify(artistService).createArtist("Controller Test Artist");
    }

    @Test
    void shouldRejectInvalidArtistRequest() throws Exception {
        // Arrange
        CreateArtistRequest clientRequestDTO = new CreateArtistRequest("");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);

        // Act + Assert
        mockMvc.perform(post("/api/artists")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(requestJson))
                 .andExpect(status().isBadRequest());
        // Verify
        verify(artistService, never()).createArtist(anyString());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingArtist() throws Exception {
        //Arrange
        when(artistService.updateArtist(100L, "Fake Controller Test Artist")).thenThrow(new ResourceNotFoundException("Artist not found"));
        CreateArtistRequest clientRequestDTO =  new CreateArtistRequest("Fake Controller Test Artist");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        //Act + Assert
        mockMvc.perform(put("/api/artists/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Artist not found"));
        //verify
        verify(artistService).updateArtist(100L, "Fake Controller Test Artist");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingArtist() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Artist not found")).when(artistService).deleteArtist(100L);

        // Act + Assert
        mockMvc.perform(delete("/api/artists/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Artist not found"));

        // Verify
        verify(artistService).deleteArtist(100L);
    }

}














package com.briansharpe.musicdiscovery.user;
import com.briansharpe.musicdiscovery.exception.*;
import com.briansharpe.musicdiscovery.user.dto.CreateUserRequest;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        // Arrange
        User savedUser = new User("TestUser", "testuser@email.com");
        when(userService.createUser("TestUser", "testuser@email.com")).thenReturn(savedUser);
        CreateUserRequest clientRequestDTO = new CreateUserRequest("TestUser", "testuser@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("TestUser"))
                .andExpect(jsonPath("$.email").value("testuser@email.com"));
        // Verify
        verify(userService).createUser("TestUser", "testuser@email.com");
    }

    @Test
    void shouldGetUserById() throws Exception {
        // Arrange
        User savedUser = new User("TestUser","testuser@email.com");
        when(userService.getUserById(77L)).thenReturn(savedUser);
        // Act + Assert
        mockMvc.perform(get("/api/users/77")).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("TestUser"))
                .andExpect(jsonPath("$.email").value("testuser@email.com"));
        // Verify
        verify(userService).getUserById(77L);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        // Arrange
        List<User> savedUsersList = new ArrayList<>();
        savedUsersList.add(new User("testUser1", "testuser1@email.com"));
        savedUsersList.add(new User("testUser2", "testuser2@email.com"));
        savedUsersList.add(new User("testUser3", "testuser3@email.com"));
        when(userService.getAllUsers()).thenReturn(savedUsersList);
        // Act + Assert
        mockMvc.perform(get("/api/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser1"))
                .andExpect(jsonPath("$[0].email").value("testuser1@email.com"))
                .andExpect(jsonPath("$[1].username").value("testUser2"))
                .andExpect(jsonPath("$[1].email").value("testuser2@email.com"))
                .andExpect(jsonPath("$[2].username").value("testUser3"))
                .andExpect(jsonPath("$[2].email").value("testuser3@email.com"));
        // Verify
        verify(userService).getAllUsers();
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Arrange
        User updatedUser = new User("UpdatedUser", "updated@email.com");
        when(userService.updateUser(12L, "UpdatedUser", "updated@email.com")).thenReturn(updatedUser);
        CreateUserRequest clientRequestDTO = new CreateUserRequest("UpdatedUser", "updated@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(put("/api/users/12").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("UpdatedUser"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
        // Verify
        verify(userService).updateUser(12L, "UpdatedUser", "updated@email.com");
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/users/100")).andExpect(status().isNoContent());
        // Verify
        verify(userService).deleteUser(100L);
    }

    @Test
    void shouldReturnNotFoundWhenUserIdMissing() throws Exception {
        // Arrange
        when(userService.getUserById(9L)).thenThrow(new ResourceNotFoundException("User not found"));
        // Act + Assert
        mockMvc.perform(get("/api/users/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
        // Verify
        verify(userService).getUserById(9L);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateUsername() throws Exception {
        // Arrange
        when(userService.createUser("TestUser", "testuser@email.com"))
                .thenThrow(new IllegalArgumentException("Username is already in use"));
        CreateUserRequest clientRequestDTO = new CreateUserRequest("TestUser", "testuser@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username is already in use"));
        // Verify
        verify(userService).createUser("TestUser", "testuser@email.com");
    }

    @Test
    void shouldReturnBadRequestWhenCreatingDuplicateEmail() throws Exception {
        // Arrange
        when(userService.createUser("TestUser", "testuser@email.com"))
                .thenThrow(new IllegalArgumentException("Email is already in use"));
        CreateUserRequest clientRequestDTO = new CreateUserRequest("TestUser", "testuser@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
        // Verify
        verify(userService).createUser("TestUser", "testuser@email.com");
    }

    @Test
    void shouldRejectInvalidUserRequest() throws Exception {
        // Arrange
        CreateUserRequest clientRequestDTO = new CreateUserRequest("", "not-validemail");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
        // Verify
        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingUser() throws Exception {
        // Arrange
        when(userService.updateUser(100L, "FakeUser", "fakeuser@email.com"))
                .thenThrow(new ResourceNotFoundException("User not found"));
        CreateUserRequest clientRequestDTO = new CreateUserRequest("FakeUser", "fakeuser@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(put("/api/users/100").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
        // Verify
        verify(userService).updateUser(100L, "FakeUser", "fakeuser@email.com");
    }


    @Test
    void shouldReturnBadRequestWhenUpdatingDuplicateUsername() throws Exception {
        // Arrange
        when(userService.updateUser(400L, "TakenUsername", "testuser@email.com"))
                .thenThrow(new IllegalArgumentException("This Username is already in use"));
        CreateUserRequest clientRequestDTO = new CreateUserRequest("TakenUsername", "testuser@email.com");
        String requestJson = objectMapper.writeValueAsString(clientRequestDTO);
        // Act + Assert
        mockMvc.perform(put("/api/users/400").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("This Username is already in use"));
        // Verify
        verify(userService).updateUser(400L, "TakenUsername", "testuser@email.com");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingUser() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(100L);
        // Act + Assert
        mockMvc.perform(delete("/api/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
        // Verify
        verify(userService).deleteUser(100L);
    }
}
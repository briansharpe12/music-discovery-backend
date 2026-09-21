package com.briansharpe.musicdiscovery.user;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() { userService = new UserService(userRepository); }

    @Test
    void shouldCreateUser() {
        // Arrange
        when(userRepository.existsByUsername("TestUser")).thenReturn(false);
        when(userRepository.existsByEmail("TestUser@email.com")).thenReturn(false);
        User savedUser = new User("TestUser", "TestUser@email.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        // Act + Assert
        User result = userService.createUser("TestUser", "TestUser@email.com");
        assertEquals("TestUser", result.getUsername());
        assertEquals("TestUser@email.com", result.getEmail());
        // Verify
        verify(userRepository).existsByUsername("TestUser");
        verify(userRepository).existsByEmail("TestUser@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername("TestUser")).thenReturn(true);
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("TestUser", "TestUser@email.com"));
        // Verify User is a duplicate, email was never checked (username fails first) + User was never saved
        verify(userRepository).existsByUsername("TestUser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername("TestUser")).thenReturn(false);
        when(userRepository.existsByEmail("TestUser@email.com")).thenReturn(true);
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("TestUser", "TestUser@email.com"));
        // Verify
        verify(userRepository).existsByUsername("TestUser");
        verify(userRepository).existsByEmail("TestUser@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        // Arrange
        User testUser = new User("TestUser", "TestUser@email.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        // Act + Assert
        User result = userService.getUserById(10L);
        assertEquals("TestUser", result.getUsername());
        assertEquals("TestUser@email.com", result.getEmail());
        // Verify
        verify(userRepository).findById(10L);
    }

    @Test
    void shouldThrowWhenUserIdNotFound() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(10L));
        // Verify
        verify(userRepository).findById(10L);
    }

    @Test
    void shouldGetAllUsers() {
        // Arrange
        User testUser1 = new User("TestUser1", "TestUser1@email.com");
        User testUser2 = new User("TestUser2", "TestUser2@email.com");
        List<User> testUserList = new ArrayList<>();
        testUserList.add(testUser1);
        testUserList.add(testUser2);
        when(userRepository.findAll()).thenReturn(testUserList);
        // Act + Assert
        List<User> result = userService.getAllUsers();
        assertEquals(testUserList, result);
        assertEquals("TestUser1", result.get(0).getUsername());
        assertEquals("TestUser2", result.get(1).getUsername());
        // Verify
        verify(userRepository).findAll();
    }

    @Test
    void shouldUpdateUser() {
        // Arrange
        User existingUser = new User("TestUsernameOld","TestUserOld@email.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("TestUsernameNew")).thenReturn(false);
        when(userRepository.existsByEmail("TestUserNew@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        // Act + Assert
        User result = userService.updateUser(5L,"TestUsernameNew", "TestUserNew@email.com");
        assertEquals("TestUsernameNew", result.getUsername());
        assertEquals("TestUserNew@email.com", result.getEmail());
        // Verify
        verify(userRepository).findById(5L);
        verify(userRepository).existsByUsername("TestUsernameNew");
        verify(userRepository).existsByEmail("TestUserNew@email.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldAllowUnchangedUsernameAndEmail() {
        // Arrange
        User testUser = new User("TestUser", "TestUser@email.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        // Act + Assert
        User result = userService.updateUser(5L,"TestUser","TestUser@email.com");
        assertEquals("TestUser", result.getUsername());
        assertEquals("TestUser@email.com", result.getEmail());
        // Verify repo called find by id + duplicate checks not needed as values did not change.+ Verify Save
        verify(userRepository).findById(5L);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldRejectDuplicateUsernameWhenUpdatingUser() {
        // Arrange
        User existingUser = new User("TestUser","TestUser@email.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("TakenUsername")).thenReturn(true);
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(5L, "TakenUsername", "TestUser@email.com"));
        // Verify ID + username lookups:    workflow stopped short of email check & save
        verify(userRepository).findById(5L);
        verify(userRepository).existsByUsername("TakenUsername");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmailWhenUpdatingUser() {
        // Arrange
        User existingUser = new User("TestUser","TestUser@email.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(5L, "TestUser", "taken@email.com"));
        // Verify ID lookup + no username lookup (unchanged) + email lookup: (duplicate = rejection) distrupted workflow, no save
        verify(userRepository).findById(5L);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository).existsByEmail("taken@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUpdatingMissingUser() {
        // Arrange
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(7L,"UpdatedUsername", "updated@email.com"));
        // Verify ID lookup:    Last step of execution with user nonexistent
        verify(userRepository).findById(7L);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        // Arrange
        User testUser = new User("DeleteUser","delete@email.com");
        when(userRepository.findById(3L)).thenReturn(Optional.of(testUser));
        // Act/Delete
        userService.deleteUser(3L);
        // Verify user was found and deleted
        verify(userRepository).findById(3L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void shouldThrowWhenDeletingMissingUser() {
        // Arrange
        when(userRepository.findById(20L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(20L));
        // Verify
        verify(userRepository).findById(20L);
        verify(userRepository, never()).delete(any(User.class));
    }
}
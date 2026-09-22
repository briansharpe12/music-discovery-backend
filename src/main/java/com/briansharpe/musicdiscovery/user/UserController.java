package com.briansharpe.musicdiscovery.user;
import com.briansharpe.musicdiscovery.user.dto.CreateUserRequest;
import com.briansharpe.musicdiscovery.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest req) {
        String username = req.username();
        String email = req.email();
        User returnedUser = userService.createUser(username, email);
        UserResponse responseEntity = new UserResponse(returnedUser.getId(), returnedUser.getUsername(), returnedUser.getEmail(), returnedUser.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User returnedUser = userService.getUserById(id);
        UserResponse responseEntity = new UserResponse(returnedUser.getId(), returnedUser.getUsername(), returnedUser.getEmail(), returnedUser.getCreatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> returnedUsers = userService.getAllUsers();
        List<UserResponse> userResponseEntities = new ArrayList<>(returnedUsers.size());
        for (User user : returnedUsers) {
            userResponseEntities.add(new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userResponseEntities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest req) {
        User updatedUser = userService.updateUser(id, req.username(), req.email());
        UserResponse responseEntity = new UserResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getCreatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(responseEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
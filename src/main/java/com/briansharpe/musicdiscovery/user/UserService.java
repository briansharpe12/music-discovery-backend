package com.briansharpe.musicdiscovery.user;
import com.briansharpe.musicdiscovery.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    public User createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {throw new IllegalArgumentException("Username is already in use");}
        if (userRepository.existsByEmail(email)) {throw new IllegalArgumentException("Email is already in use");}
        User user = new User(username, email);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, String username, String email) {
        User updatingUser = userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("User not found with provided id: " + id));
        if (!updatingUser.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("This Username is already in use");
        }
        if (!updatingUser.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("This Email is already in use");
        }
        updatingUser.setUsername(username);
        updatingUser.setEmail(email);
        return userRepository.save(updatingUser);
    }

    public void deleteUser(Long id) {
       User deleteThisUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(deleteThisUser);
    }
}
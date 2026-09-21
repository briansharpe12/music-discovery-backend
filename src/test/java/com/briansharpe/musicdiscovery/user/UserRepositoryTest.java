package com.briansharpe.musicdiscovery.user;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndRetrieveUser() {
        User testUser = new User("repo_test_user", "repo_test_user@email.com");
        User savedTestUser = userRepository.save(testUser);
        //Verify safe call worked: database/persistance values succeeded upon save
        assertNotNull(savedTestUser.getId());
        assertNotNull(savedTestUser.getCreatedAt());
        //Retrieve user + Verify fields as we anticipate
        User retrieveTestUser =  userRepository.findById(savedTestUser.getId()).orElseThrow();
        assertEquals("repo_test_user", retrieveTestUser.getUsername());
        assertEquals("repo_test_user@email.com", retrieveTestUser.getEmail());
        assertNotNull(savedTestUser.getCreatedAt());
        // delete object - only for testing
        userRepository.deleteById(savedTestUser.getId());
    }
}
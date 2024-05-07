package com.example.test_assignment.service;


import com.example.test_assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        user = new User(1L, "user@example.com", "John", "Doe", LocalDate.of(1995, 5, 20), "123 Main St", "+1234567890");
        userService.addUser(user);
    }

    @Test
    void testAddUser() {
        User newUser = new User(null, "new@example.com", "Jane", "Doe", LocalDate.of(1998, 8, 15), "456 Another St", "+19876543210");
        User savedUser = userService.addUser(newUser);
        assertNotNull(savedUser.getId());
        assertEquals("Jane", savedUser.getFirstName());
    }

    @Test
    void testUpdateUser() {
        user.setFirstName("Johnny");
        User updatedUser = userService.updateUser(user.getId(), user);
        assertEquals("Johnny", updatedUser.getFirstName());
    }

    @Test
    void testDeleteUser() {
        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(user.getId()));
        assertTrue(exception.getMessage().contains("User with ID " + user.getId() + " not found"));
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }
}
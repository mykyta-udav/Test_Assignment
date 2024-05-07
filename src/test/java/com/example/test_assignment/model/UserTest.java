package com.example.test_assignment.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAddress("123 Main St");
        user.setPhoneNumber("+1234567890");

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("+1234567890", user.getPhoneNumber());
    }

    @Test
    void testUserEquals() {
        User user1 = new User(1L, "user1@example.com", "John", "Doe", LocalDate.of(1995, 5, 20), "123 Main St", "+1234567890");
        User user2 = new User(1L, "user1@example.com", "John", "Doe", LocalDate.of(1995, 5, 20), "123 Main St", "+1234567890");
        User user3 = new User(2L, "user2@example.com", "Jane", "Doe", LocalDate.of(1996, 6, 21), "456 Another St", "+19876543210");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }
}
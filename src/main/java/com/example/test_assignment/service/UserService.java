package com.example.test_assignment.service;

import com.example.test_assignment.exception.InvalidAgeException;
import com.example.test_assignment.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();
    private Long nextId = 1L;

    @Value("${user.registration.minimum-age}")
    private int minimumAge;

    public User addUser(User user) {
        if (LocalDate.now().minusYears(minimumAge).isBefore(user.getBirthDate())) {
            throw new InvalidAgeException("InvalidAgeException: user must be at least " + minimumAge + " years old");
        }
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getBirthDate() != null) existingUser.setBirthDate(updatedUser.getBirthDate());
        if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
        if (updatedUser.getPhoneNumber() != null) existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

        return existingUser;
    }

    public void deleteUser(Long id) {
        User user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
        users.remove(user);
    }

    public List<User> searchUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'From' date must be before 'To' date.");
        }
        return users.stream()
                .filter(u -> (u.getBirthDate() != null && !u.getBirthDate().isBefore(from) && !u.getBirthDate().isAfter(to)))
                .toList();
    }

    public List<User> getAllUsers() {
        return users;
    }
}



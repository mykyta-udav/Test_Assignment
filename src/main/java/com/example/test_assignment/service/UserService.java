package com.example.test_assignment.service;

import com.example.test_assignment.dao.InMemoryDatabase;
import com.example.test_assignment.dto.request.UserRequestDto;
import com.example.test_assignment.dto.response.UserResponseDto;
import com.example.test_assignment.exception.InvalidAgeException;
import com.example.test_assignment.exception.UserNotFoundException;
import com.example.test_assignment.model.User;
import com.example.test_assignment.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Value("${user.registration.minimum-age}")
    private int minimumAge;

    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        User user = userMapper.userDtoToUser(userRequestDto);
        if (LocalDate.now().minusYears(minimumAge).isBefore(user.getBirthDate())) {
            throw new InvalidAgeException("User must be at least " + minimumAge + " years old");
        }
        Long id = InMemoryDatabase.getNextId();
        user.setId(id);
        InMemoryDatabase.getUsers().put(id, user);
        return userMapper.userToUserDto(user);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto, boolean fullUpdate) {
        User existingUser = InMemoryDatabase.getUsers().get(id);
        if (existingUser == null) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        if (fullUpdate) {
            User newUser = userMapper.userDtoToUser(userRequestDto);
            newUser.setId(id);
            InMemoryDatabase.getUsers().put(id, newUser);
            return userMapper.userToUserDto(newUser);
        } else {
            return applyPartialUpdates(existingUser, userRequestDto);
        }
    }

    public void deleteUser(Long id) {
        if (InMemoryDatabase.getUsers().remove(id) == null) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
    }

    public List<UserResponseDto> getAllUsers() {
        return InMemoryDatabase.getUsers().values().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }
    public List<UserResponseDto> searchUsersByBirthDateRange(LocalDate from, LocalDate to) {
        return InMemoryDatabase.getUsers().values().stream()
                .filter(user -> !(user.getBirthDate().isBefore(from) || user.getBirthDate().isAfter(to)))
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto applyPartialUpdates(User existingUser, UserRequestDto userRequestDto) {
        if (userRequestDto.getEmail() != null) existingUser.setEmail(userRequestDto.getEmail());
        if (userRequestDto.getFirstName() != null) existingUser.setFirstName(userRequestDto.getFirstName());
        if (userRequestDto.getLastName() != null) existingUser.setLastName(userRequestDto.getLastName());
        if (userRequestDto.getBirthDate() != null) existingUser.setBirthDate(userRequestDto.getBirthDate());
        if (userRequestDto.getAddress() != null) existingUser.setAddress(userRequestDto.getAddress());
        if (userRequestDto.getPhoneNumber() != null) existingUser.setPhoneNumber(userRequestDto.getPhoneNumber());
        InMemoryDatabase.getUsers().put(existingUser.getId(), existingUser);
        return userMapper.userToUserDto(existingUser);
    }
}
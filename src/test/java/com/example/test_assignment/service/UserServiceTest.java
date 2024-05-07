package com.example.test_assignment.service;

import com.example.test_assignment.dao.InMemoryDatabase;
import com.example.test_assignment.dto.response.UserResponseDto;
import com.example.test_assignment.exception.InvalidAgeException;
import com.example.test_assignment.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.test_assignment.model.User;
import com.example.test_assignment.dto.request.UserRequestDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "minimumAge", 18);
    }

    @Test
    void testAddUser_ValidUser() {
        UserRequestDto requestDto = new UserRequestDto("test@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890");
        User user = new User(null, requestDto.getEmail(), requestDto.getFirstName(), requestDto.getLastName(), requestDto.getBirthDate(), requestDto.getAddress(), requestDto.getPhoneNumber());

        when(userMapper.userDtoToUser(any(UserRequestDto.class))).thenReturn(user);
        when(userMapper.userToUserDto(any(User.class))).thenReturn(new UserResponseDto(1L, user.getEmail(), user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getAddress(), user.getPhoneNumber()));

        UserResponseDto responseDto = userService.addUser(requestDto);
        assertNotNull(responseDto);
        assertEquals("John", responseDto.getFirstName());
    }

    @Test
    void testAddUser_InvalidAge() {
        UserRequestDto requestDto = new UserRequestDto("test@example.com", "Jane", "Doe", LocalDate.now(), "123 Main St", "1234567890");
        User user = new User(null, requestDto.getEmail(), requestDto.getFirstName(), requestDto.getLastName(), requestDto.getBirthDate(), requestDto.getAddress(), requestDto.getPhoneNumber());

        when(userMapper.userDtoToUser(any(UserRequestDto.class))).thenReturn(user);

        assertThrows(InvalidAgeException.class, () -> userService.addUser(requestDto));
    }

    @Test
    void testUpdateUser_FullUpdate() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("updated@example.com");
        requestDto.setFirstName("John");
        requestDto.setLastName("Updated");
        requestDto.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDto.setAddress("123 Updated St");
        requestDto.setPhoneNumber("0987654321");
        User existingUser = new User(1L, "test@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890");
        InMemoryDatabase.getUsers().put(1L, existingUser);
        when(userMapper.userDtoToUser(any(UserRequestDto.class))).thenReturn(new User(1L, requestDto.getEmail(), requestDto.getFirstName(), requestDto.getLastName(), requestDto.getBirthDate(), requestDto.getAddress(), requestDto.getPhoneNumber()));
        when(userMapper.userToUserDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponseDto(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getAddress(), user.getPhoneNumber());
        });
        UserResponseDto updatedUser = userService.updateUser(1L, requestDto, true);
        assertEquals("Updated", updatedUser.getLastName());
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        User existingUser = new User(1L, "original@example.com", "Original", "User", LocalDate.of(1990, 1, 1), "Original Address", "1234567890");
        InMemoryDatabase.getUsers().put(1L, existingUser);
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("updated@example.com");
        requestDto.setAddress("Updated Address");
        when(userMapper.userDtoToUser(any(UserRequestDto.class))).thenAnswer(invocation -> {
            UserRequestDto dto = invocation.getArgument(0);
            existingUser.setEmail(dto.getEmail() != null ? dto.getEmail() : existingUser.getEmail());
            existingUser.setAddress(dto.getAddress() != null ? dto.getAddress() : existingUser.getAddress());
            return existingUser;
        });
        when(userMapper.userToUserDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponseDto(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getAddress(), user.getPhoneNumber());
        });
        UserResponseDto updatedUser = userService.updateUser(1L, requestDto, false);
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated Address", updatedUser.getAddress());
        assertEquals("Original", updatedUser.getFirstName());  // Should remain unchanged
        assertEquals("User", updatedUser.getLastName());       // Should remain unchanged
        assertEquals(LocalDate.of(1990, 1, 1), updatedUser.getBirthDate());  // Should remain unchanged
        assertEquals("1234567890", updatedUser.getPhoneNumber()); // Should remain unchanged
    }

    @Test
    void testDeleteUser() {
        User existingUser = new User(1L, "test@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890");
        InMemoryDatabase.getUsers().put(1L, existingUser);

        userService.deleteUser(1L);
        assertFalse(InMemoryDatabase.getUsers().containsKey(1L));
    }
}
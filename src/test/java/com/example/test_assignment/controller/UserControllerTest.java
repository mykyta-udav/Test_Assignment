package com.example.test_assignment.controller;

import com.example.test_assignment.dto.request.UserRequestDto;
import com.example.test_assignment.dto.response.UserResponseDto;
import com.example.test_assignment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setup() {
        userRequestDto = new UserRequestDto(); // Populate with test data
        userResponseDto = new UserResponseDto(); // Populate with test data
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.addUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));

        verify(userService, times(1)).addUser(any(UserRequestDto.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserRequestDto.class), eq(true))).thenReturn(userResponseDto);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));

        verify(userService, times(1)).updateUser(anyLong(), any(UserRequestDto.class), eq(true));
    }

    @Test
    void testPatchUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserRequestDto.class), eq(false))).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(anyLong(), any(UserRequestDto.class), eq(false));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserResponseDto> users = Collections.singletonList(userResponseDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(userResponseDto.getEmail()));

        verify(userService, times(1)).getAllUsers();
    }
    @Test
    void testSearchUsersByBirthDateRange() throws Exception {
        List<UserResponseDto> users = Collections.singletonList(userResponseDto);
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.now();
        when(userService.searchUsersByBirthDateRange(from, to)).thenReturn(users);

        mockMvc.perform(get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk());

        verify(userService, times(1)).searchUsersByBirthDateRange(from, to);
    }

}
package com.example.test_assignment.service.mapper;

import com.example.test_assignment.dto.request.UserRequestDto;
import com.example.test_assignment.dto.response.UserResponseDto;
import com.example.test_assignment.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userDtoToUser(UserRequestDto userRequestDto);
    UserResponseDto userToUserDto(User user);
}

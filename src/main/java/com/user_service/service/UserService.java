package com.user_service.service;

import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.enums.UserRole;

import java.util.List;

public interface UserService {

    UserResponseDto getById(Long id);

    UserAuthDto getByUsername(String username);

    List<UserResponseDto> search(UserFilterDto filterDto);

    UserResponseDto createWithRole(UserRequestDto dto, UserRole role);

    void update(Long id, UserUpdateDto dto);

    void delete(Long id);
}

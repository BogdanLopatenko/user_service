package com.user_service.service;

import com.user_service.dto.UserRequestDto;
import com.user_service.dto.UserResponseDto;
import com.user_service.dto.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.enums.UserRole;

import java.util.List;

public interface UserService {

    UserResponseDto getById(Long id);

    List<UserResponseDto>  search(UserFilterDto filterDto);

    UserResponseDto createWithRole(UserRequestDto dto, UserRole role);

    void update(UserUpdateDto dto);

    void delete(Long id);
}

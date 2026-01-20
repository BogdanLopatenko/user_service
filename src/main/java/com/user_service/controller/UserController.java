package com.user_service.controller;

import com.user_service.api.UserApi;
import com.user_service.dto.confirmation.UserEmailConformationResponseDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.enums.UserRole;
import com.user_service.service.UserEmailConfirmationService;
import com.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    private final UserEmailConfirmationService userEmailConfirmationService;

    @GetMapping("{id}")
    public UserResponseDto getById(@PathVariable Long id) {

        return userService.getById(id);
    }

    @GetMapping("/by-username")
    public UserAuthDto getByUsername(@RequestParam("username") String username) {

        return userService.getByUsername(username);
    }

    @PostMapping("/search")
    public List<UserResponseDto> search(UserFilterDto filterDto) {

        return userService.search(filterDto);
    }

    @PostMapping("/new")
    public UserResponseDto create(@RequestBody UserRequestDto dto) {

        return userService.createWithRole(dto, UserRole.USER);
    }

    @PostMapping
    public UserResponseDto createWithRole(@RequestBody UserRequestDto dto, @RequestParam UserRole userRole) {

        return userService.createWithRole(dto, userRole);
    }

    @GetMapping("/verification-token/{userId}")
    public String generateEmailVerificationToken(@PathVariable Long userId){

        UserEmailConformationResponseDto userEmailConformationResponseDto = userEmailConfirmationService.create(userId);

        return String.valueOf(userEmailConformationResponseDto.getToken());
    }
}

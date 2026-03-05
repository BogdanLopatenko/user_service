package com.user_service.controller;

import com.user_service.api.UserServiceApi;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.enums.UserRole;
import com.user_service.service.EmailConfirmationService;
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
public class UserServiceServiceController  implements UserServiceApi {

    private final UserService userService;

    private final EmailConfirmationService emailConfirmationService;

    @GetMapping("/by-username/{username}")
    public UserAuthDto getByUsername(@PathVariable String username) {

        return userService.getByUsername(username);
    }

    @GetMapping("/by-token/{token}")
    public UserResponseDto getUserByConfirmationToken(@PathVariable String token) {

        return emailConfirmationService.getUserByConfirmationToken(token);
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

    @GetMapping("/generate-token/{userId}")
    public String generateEmailConfirmationToken(@PathVariable Long userId) {

        return String.valueOf(emailConfirmationService.create(userId).getToken());
    }

    @GetMapping("/verify-email/{token}")
    public void verifyUserEmail(@PathVariable String token) {

        emailConfirmationService.confirmEmail(token);
    }

}

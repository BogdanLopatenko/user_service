package com.user_service.controller;

import com.user_service.dto.UserRequestDto;
import com.user_service.dto.UserResponseDto;
import com.user_service.dto.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.enums.UserRole;
import com.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("{id}")
    public UserResponseDto getById(@PathVariable Long id){

        return userService.getById(id);
    }

    @PostMapping("/search")
    public List<UserResponseDto> search(UserFilterDto filterDto){

        return userService.search(filterDto);
    }


    @PostMapping
    public UserResponseDto create(@RequestBody UserRequestDto dto, @RequestParam(required = false) UserRole userRole) {

        return userService.createWithRole(dto, userRole);
    }

    @PutMapping
    public void update(UserUpdateDto updateDto){

        userService.update(updateDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){

        userService.delete(id);
    }
}

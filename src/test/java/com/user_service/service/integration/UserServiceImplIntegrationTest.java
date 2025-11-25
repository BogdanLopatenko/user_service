package com.user_service.service.integration;

import com.user_service.dto.UserRequestDto;
import com.user_service.dto.UserResponseDto;
import com.user_service.dto.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import com.user_service.repository.UserRepository;
import com.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {

        User user1 = new User(null, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, true);
        User user2 = new User(null, "someusername2", "somepassword", "somefirstname", "somelastname", "someemail3@gmail.com", UserRole.USER, true);

        savedUser = userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @DisplayName("Integration test: should successfully fetch user from actual db")
    void getById_UserExistsInDB_ReturnDto() {

        UserResponseDto result = userService.getById(savedUser.getId());

        assertNotNull(result, "Result must not be null");
        assertEquals(savedUser.getId(), result.getId(), "Id must be the same");
    }

    @Test
    @DisplayName("Integration test; should throw exception when user ID is not found in actual DB")
    void getById_UserNotFound_ThrowsException() {

        Long NOT_EXISTED_ID = savedUser.getId() + 100L;

        assertThrows(UserNotFoundException.class, () -> {
            userService.getById(NOT_EXISTED_ID);
        }, "Should thrown UserNotFoundException");
    }

    @Test
    @DisplayName("Should return list with user")
    void search_Success() {

        UserFilterDto userFilterDto = new UserFilterDto(null, null, null, null, null, true);

        List<UserResponseDto> result = userService.search(userFilterDto);

        assertEquals(result.get(0).getIsActive(), savedUser.getIsActive(), "List should contains user");
    }

    @Test
    @DisplayName("Should create user")
    void createWithRole_Success() {

        UserRequestDto user = new UserRequestDto("someusername3", "somepassword", "somename", "somelastname", "someemail2@gmail.com");

        UserResponseDto result = userService.createWithRole(user, UserRole.ADMIN);

        assertNotNull(result, "User should be created");
    }

    @Test
    @DisplayName("Should throw EmailIsAlreadyExistException")
    void createWithRole_EmailIsAlreadyExistException() {

        UserRequestDto user = new UserRequestDto("someusername45", "somepassword", "somename", "somelastname", "someemail@gmail.com");

        assertThrows(EmailIsAlreadyExistException.class, () -> {
            userService.createWithRole(user, UserRole.USER);
        }, "Should throw exception");
    }

    @Test
    @DisplayName("Should throw UsernameIsAlreadyExistException")
    void createWithRole_UsernameIsAlreadyExistException() {

        UserRequestDto user = new UserRequestDto("someusername", "somepassword", "somename", "somelastname", "someemail2@gmail.com");

        assertThrows(UsernameIsAlreadyExistException.class, () -> {
            userService.createWithRole(user, UserRole.USER);
        }, "Should throw exception");
    }

    @Test
    @DisplayName("Should update user")
    void update_Successfully() {

        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.ADMIN, true);

        userService.update(userUpdateDto);

        UserResponseDto userById = userService.getById(1L);

        assertEquals(UserRole.ADMIN, userById.getRole(), "Roles must be equal");
        assertEquals(userUpdateDto.getEmail(), userById.getEmail());
    }
}

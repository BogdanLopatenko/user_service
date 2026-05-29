package com.user_service.service.unit;

import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameAlreadyExistException;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.UserRepository;
import com.user_service.service.UserTestBuilder;
import com.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final User user = new UserTestBuilder().build();

    private final User updatedUser = new UserTestBuilder().withStatus(UserStatus.ACTIVE).build();
    private final UserResponseDto userResponseDto = new UserTestBuilder().buildResponseDto();

    private final UserRequestDto userRequestDto = new UserTestBuilder().buildRequestDto();

    private final UserUpdateDto userUpdateDto = new UserTestBuilder().withStatus(UserStatus.ACTIVE).buildUpdateDto();




    @Test
    @DisplayName("Should return response dto when user exists")
    void getById_UserExists_ReturnDto() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(user.getId());

        assertNotNull(result, "Result can't be null");
        assertEquals(userResponseDto.getId(), result.getId(), "ID Dto must be the same");

        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toResponseDto(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void getById_UserNotFound_ThrowsException() {

        Long notExistingId = 20900909L;

        when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getById(notExistingId);
        }, "Must thrown exception UserNotFoundException");

        verify(userMapper, never()).toResponseDto(any(User.class));
    }


    @Test
    @DisplayName("Should create user successfully.")
    void createWithRole_Success_ReturnDto() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        when(userMapper.toEntityFromRequestDto(userRequestDto)).thenReturn(user);

        when(userRepository.save(user)).thenReturn(user);

        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createWithRole(userRequestDto, UserRole.USER);

        assertNotNull(result, "Result must not be null");
        assertEquals(user.getId(), userResponseDto.getId(), "Id must be equal");
        assertEquals(userResponseDto.getRole(), UserRole.USER);
        assertEquals(userResponseDto.getStatus(), UserStatus.NEED_EMAIL_CONFIRMATION);

        verify(userRepository, times(1)).findByUsername(userRequestDto.getUsername());
        verify(userRepository, times(1)).findByEmail(userRequestDto.getEmail());
        verify(userMapper, times(1)).toEntityFromRequestDto(userRequestDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponseDto(user);
    }

    @Test
    @DisplayName("Should throw UsernameIsAlreadyExistException")
    void createWithRole_UsernameAlreadyExist_ThrowsException() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistException.class, () -> {
            userService.createWithRole(userRequestDto, UserRole.USER);
        }, "Username must be unique");

        verify(userRepository, times(1)).findByUsername(userRequestDto.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UsernameIsAlreadyExistException")
    void createWithRole_EmailAlreadyExist_ThrowsException() {

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> {
            userService.createWithRole(userRequestDto, UserRole.USER);
        }, "Email must be unique");

        verify(userRepository, times(1)).findByEmail(userRequestDto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user successfully")
    void update_Success_ReturnNothing() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);

        userService.update(user.getId(), userUpdateDto);

        verify(userMapper).updateFromDb(userUpdateDto, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException")
    void update_UserNotFound_ThrowsException() {

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.update(2L, userUpdateDto);
        }, "User not found by ID");

        verify(userRepository, times(1)).findById(2L);
        verify(userMapper, never()).updateFromDb(userUpdateDto, user);
    }
}

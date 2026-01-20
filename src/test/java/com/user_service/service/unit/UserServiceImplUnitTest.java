package com.user_service.service.unit;

import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.UserRepository;
import com.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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

    private final Long userId = 1L;
    private final User fullUser1 = new User(userId, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, UserStatus.ACTIVE);
    private final User fullUser2 = new User(userId + 1, "bogdan", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, UserStatus.ACTIVE);

    private final User unfilledUser1 = new User(null, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", null, null);
    private final User user1WithoutId = new User(null, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, UserStatus.ACTIVE);
    private final UserResponseDto responseDtoUser1 = new UserResponseDto(userId, "someusername", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, UserStatus.ACTIVE);
    private final UserResponseDto responseDtoUser2 = new UserResponseDto(userId + 1, "bogdan", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.USER, UserStatus.ACTIVE);

    private final UserUpdateDto userUpdateDto1 = new UserUpdateDto(userId, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.ADMIN, UserStatus.ACTIVE);
    private final User changedUser1 = new User(userId, "someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com", UserRole.ADMIN, UserStatus.ACTIVE);
    private final UserRequestDto requestDtoUser1 = new UserRequestDto("someusername", "somepassword", "somefirstname", "somelastname", "someemail@gmail.com");
    private final UserFilterDto filterDto = new UserFilterDto("bogdan", "", "", "", null, null);
    private final List<User> userList = List.of(fullUser1, fullUser2);
    private final List<UserResponseDto> responseDtoList = List.of(responseDtoUser1, responseDtoUser2);


    @Test
    @DisplayName("Should return response dto when user exists")
    void getById_UserExists_returnDto() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(fullUser1));

        when(userMapper.toResponseDto(fullUser1)).thenReturn(responseDtoUser1);

        UserResponseDto result = userService.getById(userId);

        assertNotNull(result, "Result can't be null");
        assertEquals(responseDtoUser1.getId(), result.getId(), "ID Dto must be the same");

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponseDto(fullUser1);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void getById_UserNotFound_ThrowsException() {

        Long notExistingId = 2L;

        when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getById(notExistingId);
        }, "Must thrown exception UserNotFoundException");

        verify(userMapper, never()).toResponseDto(any(User.class));
    }

    @Test
    @DisplayName("Should return dto list")
    void search_UsersList_returnResponseList() {

        when(userRepository.findAll(any(Specification.class))).thenReturn(userList);
        when(userMapper.toResponseDtoList(userList)).thenReturn(responseDtoList);

        List<UserResponseDto> result = userService.search(filterDto);

        assertNotNull(result, "Result can't be null");
        assertEquals(userList.size(), responseDtoList.size(), "Lists sizes must be equal");
        assertEquals(responseDtoList.get(0).getUsername(), responseDtoList.get(0).getUsername());

        verify(userRepository, times(1)).findAll(any(Specification.class));
        verify(userMapper, times(1)).toResponseDtoList(userList);
    }

    @Test
    @DisplayName("Should create user successfully.")
    void createWithRole_Success() {

        //no duplicates
        when(userRepository.findByUsername(requestDtoUser1.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDtoUser1.getEmail())).thenReturn(Optional.empty());

        when(userMapper.toEntityFromRequestDto(requestDtoUser1)).thenReturn(unfilledUser1);

        when(userRepository.save(user1WithoutId)).thenReturn(fullUser1);

        when(userMapper.toResponseDto(fullUser1)).thenReturn(responseDtoUser1);

        UserResponseDto result = userService.createWithRole(requestDtoUser1, UserRole.USER);

        assertNotNull(result, "Result must not be null");
        assertEquals(fullUser1.getId(), responseDtoUser1.getId(), "Id must be equal");

        verify(userRepository, times(1)).findByUsername(requestDtoUser1.getUsername());
        verify(userRepository, times(1)).findByEmail(requestDtoUser1.getEmail());
        verify(userMapper, times(1)).toEntityFromRequestDto(requestDtoUser1);
        verify(userRepository, times(1)).save(user1WithoutId);
        verify(userMapper, times(1)).toResponseDto(fullUser1);
    }

    @Test
    @DisplayName("Should throw UsernameIsAlreadyExistException")
    void createWithRole_throwsUsernameAlreadyExistException() {

        when(userRepository.findByUsername(requestDtoUser1.getUsername())).thenReturn(Optional.of(fullUser1));

        assertThrows(UsernameIsAlreadyExistException.class, () -> {
            userService.createWithRole(requestDtoUser1, UserRole.USER);
        }, "Username must be unique");

        verify(userRepository, times(1)).findByUsername(requestDtoUser1.getUsername());
    }

    @Test
    @DisplayName("Should throw UsernameIsAlreadyExistException")
    void createWithRole_throwsEmailAlreadyExistException() {

        when(userRepository.findByEmail(requestDtoUser1.getEmail())).thenReturn(Optional.of(fullUser1));

        assertThrows(EmailIsAlreadyExistException.class, () -> {
            userService.createWithRole(requestDtoUser1, UserRole.USER);
        }, "Email must be unique");

        verify(userRepository, times(1)).findByEmail(requestDtoUser1.getEmail());
    }

    @Test
    @DisplayName("Should update user without exceptions")
    void update_Successfully() {

        when(userRepository.findById(userUpdateDto1.getId())).thenReturn(Optional.of(fullUser1));

        when(userRepository.save(any(User.class))).thenReturn(changedUser1);

        userService.update(userUpdateDto1);

        verify(userRepository, times(1)).findById(userUpdateDto1.getId());
        verify(userMapper, times(1)).updateFromDb(userUpdateDto1, fullUser1);
        assertEquals(UserRole.ADMIN, changedUser1.getRole(), "User should be updated by mapper");
        verify(userRepository, times(1)).save(fullUser1);
    }
}

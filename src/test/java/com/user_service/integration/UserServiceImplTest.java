package com.user_service.integration;

import com.user_service.AbstractIntegrationTest;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameAlreadyExistException;
import com.user_service.repository.UserRepository;
import com.user_service.service.UserService;
import com.user_service.util.builder.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

@Transactional
@AutoConfigureMockMvc
public class UserServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void getById_Success_ShouldReturnUser() {

        User user = new UserTestBuilder().withId(null).build();

        User savedUser = userRepository.save(user);

        UserResponseDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(result.getRole(), UserRole.USER);
        assertEquals(result.getStatus(), UserStatus.NEED_EMAIL_CONFIRMATION);
    }

    @Test
    void getById_UserNotFound_ThrowsException() {

        assertThrows(UserNotFoundException.class, () -> {

            userService.getById(123L);
        });
    }

    @Test
    void createWithRole_Success_ShouldCreateUserWithRoleAndStatus() {

        UserRequestDto userRequestDto = new UserTestBuilder().buildRequestDto();

        UserResponseDto result = userService.createWithRole(userRequestDto, UserRole.USER);

        assertNotNull(result);
        assertEquals(result.getStatus(), UserStatus.NEED_EMAIL_CONFIRMATION);
        assertEquals(result.getRole(), UserRole.USER);
        assertNotNull(result.getId());
    }

    @Test
    void createWithRole_EmailAlreadyExist_ThrowsException() {

        User user = new UserTestBuilder().withId(null).withUsername("someotherusername").build();
        userRepository.save(user);

        UserRequestDto userRequestDto = new UserTestBuilder().buildRequestDto();

        assertThrows(EmailAlreadyExistException.class, () -> {

            userService.createWithRole(userRequestDto, UserRole.USER);
        });
    }

    @Test
    void createWithRole_UsernameAlreadyExist_ThrowsException() {

        User user = new UserTestBuilder().withId(null).build();
        userRepository.save(user);

        UserRequestDto userRequestDto = new UserTestBuilder().buildRequestDto();

        assertThrows(UsernameAlreadyExistException.class, () -> {

            userService.createWithRole(userRequestDto, UserRole.USER);
        });
    }

    @Test
    void update_Success_ShouldUpdateUserInDb() {

        User user = new UserTestBuilder().withId(null).build();

        User savedUser = userRepository.save(user);

        UserUpdateDto updateUserDto = new UserTestBuilder().withUsername("werwerwer").buildUpdateDto();

        userService.update(savedUser.getId(), updateUserDto);

        UserResponseDto result = userService.getById(savedUser.getId());

        assertEquals(result.getUsername(), updateUserDto.getUsername());
    }

    @Test
    void update_DataIntegrityViolation_ThrowsException() {

        User user = new UserTestBuilder().withUsername("234234").withId(null).build();
        User user2 = new UserTestBuilder().withEmail("newEmail@gmail.com").withId(null).build();

        User savedUser = userRepository.save(user);
        userRepository.save(user2);

        UserUpdateDto updateUserDto1 = new UserTestBuilder().withEmail("newEmail@gmail.com").buildUpdateDto();

        assertThrows(DataIntegrityViolationException.class, () -> {

            userService.update(savedUser.getId(), updateUserDto1);

            userRepository.flush();
        });
    }

    @Test
    void update_UserNotFound_ThrowsException() {

        User user = new UserTestBuilder().withId(null).build();

        UserUpdateDto updateDto = new UserTestBuilder().buildUpdateDto();

        userRepository.save(user);

        assertThrows(UserNotFoundException.class, () -> {

            userService.update(212L, updateDto);
        });
    }

    @Test
    void getByUsername_Success_ShouldReturnUser() {

        User user = new UserTestBuilder().withId(null).build();
        userRepository.save(user);

        UserAuthDto result = userService.getByUsername(user.getUsername());

        assertEquals(result.getUsername(), user.getUsername());
        assertNotNull(result);
    }

    @Test
    void getByUsername_UserNotFound_ThrowsException() {

        User user = new UserTestBuilder().withId(null).build();
        userRepository.save(user);

        assertThrows(UserNotFoundException.class, () -> {
            userService.getByUsername("fakeUsername");
        });
    }

    @Test
    void search_ShouldFindNothing() {

        UserFilterDto userFilterDto = new UserFilterDto();

        List<UserResponseDto> result = userService.search(userFilterDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_ByRole_ReturnsOnlyWithRoleAdmin() {

        User userAdmin = new UserTestBuilder()
                .withUsername("UserAdmin")
                .withEmail("useradmin@gmail.com")
                .withId(null)
                .withRole(UserRole.ADMIN)
                .build();

        User commonUser = new UserTestBuilder()
                .withId(null)
                .build();

        userRepository.saveAll(List.of(userAdmin, commonUser));

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setRole(UserRole.ADMIN);


        List<UserResponseDto> result = userService.search(filterDto);

        assertEquals(result.size(), 1);
        assertTrue(!result.isEmpty());
        assertTrue(result.get(0).getUsername().equalsIgnoreCase("userAdmin"));
        assertTrue(result.get(0).getEmail().equalsIgnoreCase("useradmin@gmail.com"));
        assertEquals(result.get(0).getRole(), UserRole.ADMIN);
    }

    @Test
    void search_ByStatus_ReturnsOnlyWithStatusActive() {

        User activeUser = new UserTestBuilder()
                .withUsername("UserAdmin")
                .withEmail("useradmin@gmail.com")
                .withId(null)
                .withStatus(UserStatus.ACTIVE)
                .withRole(UserRole.ADMIN)
                .build();

        User needVerificationUser = new UserTestBuilder()
                .withId(null)
                .build();

        userRepository.saveAll(List.of(activeUser, needVerificationUser));

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setStatus(UserStatus.ACTIVE);

        List<UserResponseDto> result = userService.search(filterDto);

        assertEquals(result.size(), 1);
        assertTrue(!result.isEmpty());
        assertTrue(result.get(0).getUsername().equalsIgnoreCase("userAdmin"));
        assertTrue(result.get(0).getEmail().equalsIgnoreCase("useradmin@gmail.com"));
        assertEquals(result.get(0).getStatus(), UserStatus.ACTIVE);
    }


}

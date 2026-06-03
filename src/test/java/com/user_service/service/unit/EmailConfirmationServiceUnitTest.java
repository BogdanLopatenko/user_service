package com.user_service.service.unit;

import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailConfirmationNotFoundException;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.mapper.EmailConfirmationMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.EmailConfirmationRepository;
import com.user_service.repository.UserRepository;
import com.user_service.service.impl.EmailConfirmationServiceImpl;
import com.user_service.util.builder.EmailConfirmationTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.user_service.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmailConfirmationServiceUnitTest {

    private Short expirationDurabilityInHours = 6;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailConfirmationMapper emailConfirmationMapper;

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    private Clock clock = Clock.fixed(
            Instant.parse("2026-05-28T12:00:00Z"),
            ZoneOffset.UTC
    );

    private EmailConfirmationServiceImpl emailConfirmationService;

    @BeforeEach
    void setUp() {

        emailConfirmationService =
                new EmailConfirmationServiceImpl(
                        expirationDurabilityInHours,
                        userRepository,
                        userMapper,
                        emailConfirmationMapper,
                        emailConfirmationRepository,
                        clock
                );
    }

    private final EmailConfirmationResponseDto emailConfirmationResponseDto = new EmailConfirmationTestBuilder().buildResponseDto();


    @Test
    @DisplayName("Should create email confirmation and return dto")
    void create_Success_ReturnDto() {

        User user = initUser();

        EmailConfirmation emailConfirmation = initEmailConfirmation();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(emailConfirmationMapper.construct(
                eq(user),
                any(LocalDateTime.class),
                eq(false)
        )).thenReturn(emailConfirmation);
        when(emailConfirmationRepository.save(emailConfirmation)).thenReturn(emailConfirmation);
        when(emailConfirmationMapper.toResponseDto(emailConfirmation)).thenReturn(emailConfirmationResponseDto);

        EmailConfirmationResponseDto emailConfirmationResponseDto = emailConfirmationService.create(user.getId());

        assertNotNull(emailConfirmationResponseDto, "result can't be null");
        assertEquals(emailConfirmationResponseDto.getToken(), emailConfirmation.getToken(), "Must be the same tokens");
        assertEquals(emailConfirmationResponseDto.getIsUsed(), emailConfirmation.getIsUsed());
    }

    @Test
    @DisplayName("Should throw a UserNotFoundException when user does not exist")
    void create_UserNotFound_ThrowsException() {

        User user = initUser();
        EmailConfirmation emailConfirmation = initEmailConfirmation();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> {
                    emailConfirmationService.create(user.getId());
                },
                "User not found by ID"
        );

        verify(emailConfirmationMapper, never()).construct(any(User.class), any(LocalDateTime.class), eq(false));
        verify(emailConfirmationRepository, never()).save(emailConfirmation);
        verify(emailConfirmationMapper, never()).toResponseDto(emailConfirmation);
    }

    @Test
    @DisplayName("Should change users status and confirmation flag")
    void confirmEmail_Success_ChangeUserStatusToActive() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findById(emailConfirmation.getToken())).thenReturn(Optional.of(emailConfirmation));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        emailConfirmationService.confirmEmail(String.valueOf(emailConfirmation.getToken()));

        assertEquals(user.getStatus(), UserStatus.ACTIVE);
        assertTrue(emailConfirmation.getIsUsed());

        verify(emailConfirmationRepository, times(1)).save(emailConfirmation);
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("")
    void confirmEmail_EmailConfirmationNotFoundException_ThrowsException() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findById(emailConfirmation.getToken()))
                .thenReturn(Optional.empty());

        assertThrows(EmailConfirmationNotFoundException.class, () -> {

            emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));
        }, "Should throw EmailConfirmationNotFoundException when emailConfirmation not found by token");

        verify(emailConfirmationRepository, never()).save(emailConfirmation);
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Should throw EmailConfirmationTokenExpirationException when expiration date is on ")
    void confirmEmail_EmailConfirmationTokenExpiration_ThrowsException() {

        EmailConfirmation expiredEmailConfirmation = new EmailConfirmationTestBuilder()
                .withExpiresAt(LocalDateTime.of(2026, 5, 28, 10, 0))
                .build();

        when(emailConfirmationRepository.findById(any()))
                .thenReturn(Optional.of(expiredEmailConfirmation));

        assertThrows(EmailConfirmationTokenExpirationException.class, () -> {

            emailConfirmationService.confirmEmail(String.valueOf(expiredEmailConfirmation.getToken()));
        }, "Should throw EmailConfirmationTokenExpirationException when token is expired");

        verify(emailConfirmationRepository, never()).save(expiredEmailConfirmation);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw EmailAlreadyActivatedException when isUsed flag == true")
    void confirmEmail_EmailAlreadyActivated_ThrowsException() {

        User user = initUser();

        EmailConfirmation confirmedEmailConfirmation = new EmailConfirmationTestBuilder()
                .withUsed(true)
                .build();

        when(emailConfirmationRepository.findById(confirmedEmailConfirmation.getToken()))
                .thenReturn(Optional.of(confirmedEmailConfirmation));

        assertThrows(EmailAlreadyActivatedException.class, () -> {

            emailConfirmationService.confirmEmail(String.valueOf(confirmedEmailConfirmation.getToken()));
        }, "Should throw EmailAlreadyActivatedException when isUsed flag == true");

        verify(emailConfirmationRepository, never()).save(confirmedEmailConfirmation);
        verify(userRepository, never()).findById(user.getId());
    }

    @Test
    @DisplayName("Should find User by confirmation token and return UserResponseDto")
    void getUserByConfirmationToken_Success_ReturnDto() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();
        UserResponseDto userResponseDto = initUserResponseDto();

        when(emailConfirmationRepository.findById(emailConfirmation.getToken()))
                .thenReturn(Optional.of(emailConfirmation));
        when(userMapper.toResponseDto(user))
                .thenReturn(userResponseDto);

        UserResponseDto userByConfirmationToken =
                emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));

        assertEquals(userByConfirmationToken.getId(), user.getId());
    }

    @Test
    @DisplayName("Should throw EmailConfirmationNotFoundException when token not found")
    void getUserByConfirmationToken_EmailConfirmationNotFound_ThrowsException() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findById(emailConfirmation.getToken()))
                .thenReturn(Optional.empty());

        assertThrows(EmailConfirmationNotFoundException.class, () -> {

            emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));
        }, "Should throw EmailConfirmationNotFoundException when emailConfirmation not found by token");

        verify(userMapper, never()).toResponseDto(user);
    }


}

package com.user_service.unit;

import com.user_service.config.properties.EmailConfigurationProperties;
import com.user_service.constant.TestConstant;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static com.user_service.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmailConfirmationServiceImplTest {

    @Mock
    private EmailConfigurationProperties emailConfigurationProperties;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailConfirmationMapper emailConfirmationMapper;

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    private Clock clock = Clock.fixed(TestConstant.DEFAULT_INSTANT, ZoneId.systemDefault());

    private EmailConfirmationServiceImpl emailConfirmationService;

    @BeforeEach
    void setUp() {

        emailConfirmationService =
                new EmailConfirmationServiceImpl(
                        emailConfigurationProperties,
                        userRepository,
                        userMapper,
                        emailConfirmationMapper,
                        emailConfirmationRepository,
                        clock
                );
    }

    @Test
    void shouldReturnEmailConfirmationDtoWhenUserExists() {

        User user = initUser();
        EmailConfirmationResponseDto emailConfirmationResponseDto = initEmailConfirmationResponseDto();
        EmailConfirmation emailConfirmation = initEmailConfirmation();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(emailConfirmationMapper.construct(
                eq(user),
                any(LocalDateTime.class),
                eq(false)
        )).thenReturn(emailConfirmation);
        when(emailConfirmationRepository.save(emailConfirmation)).thenReturn(emailConfirmation);
        when(emailConfirmationMapper.toResponseDto(emailConfirmation)).thenReturn(emailConfirmationResponseDto);

        EmailConfirmationResponseDto emailConfirmationResponseDto2 = emailConfirmationService.create(user.getId());

        assertNotNull(emailConfirmationResponseDto2, "result can't be null");
        assertEquals(emailConfirmationResponseDto2.getToken(), emailConfirmation.getToken(), "Must be the same tokens");
        assertEquals(emailConfirmationResponseDto2.getIsUsed(), emailConfirmation.getIsUsed());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

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
    void shouldChangeUserStatusToActiveWhenEmailConfirmationIsSuccessful() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findByToken(emailConfirmation.getToken())).thenReturn(Optional.of(emailConfirmation));
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
    void shouldThrowEmailConfirmationNotFoundExceptionWhenEmailConfirmationDoesNotExist() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findByToken(emailConfirmation.getToken()))
                .thenReturn(Optional.empty());

        assertThrows(EmailConfirmationNotFoundException.class, () -> {

            emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));
        }, "Should throw EmailConfirmationNotFoundException when emailConfirmation not found by token");

        verify(emailConfirmationRepository, never()).save(emailConfirmation);
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldThrowEmailConfirmationTokenExpirationExceptionWhenTokenIsExpired() {

        EmailConfirmation expiredEmailConfirmation = new EmailConfirmationTestBuilder()
                .withExpiresAt(LocalDateTime.of(2025, 1, 1, 1, 1))
                .build();

        when(emailConfirmationRepository.findByToken(any()))
                .thenReturn(Optional.of(expiredEmailConfirmation));

        assertThrows(EmailConfirmationTokenExpirationException.class, () -> {

            emailConfirmationService.confirmEmail(String.valueOf(expiredEmailConfirmation.getToken()));
        }, "Should throw EmailConfirmationTokenExpirationException when token is expired");

        verify(emailConfirmationRepository, never()).save(expiredEmailConfirmation);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowEmailAlreadyActivatedExceptionWhenEmailIsAlreadyActivated() {

        EmailConfirmation confirmedEmailConfirmation = new EmailConfirmationTestBuilder()
                .withExpiresAt(LocalDateTime.of(2025, 2, 1, 1, 1))
                .withUsed(true)
                .build();

        when(emailConfirmationRepository.findByToken(any(UUID.class)))
                .thenReturn(Optional.of(confirmedEmailConfirmation));

        String token = confirmedEmailConfirmation.getToken().toString();

        assertThrows(EmailAlreadyActivatedException.class, () -> {
            emailConfirmationService.confirmEmail(token);
        });

        verify(emailConfirmationRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldReturnUserResponseDtoWhenConfirmationTokenExists() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();
        UserResponseDto userResponseDto = initUserResponseDto();

        when(emailConfirmationRepository.findByToken(emailConfirmation.getToken()))
                .thenReturn(Optional.of(emailConfirmation));
        when(userMapper.toResponseDto(user))
                .thenReturn(userResponseDto);

        UserResponseDto userByConfirmationToken =
                emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));

        assertEquals(userByConfirmationToken.getId(), user.getId());
    }

    @Test
    void shouldThrowEmailConfirmationNotFoundExceptionWhenConfirmationTokenDoesNotExist() {

        EmailConfirmation emailConfirmation = initEmailConfirmation();
        User user = initUser();

        when(emailConfirmationRepository.findByToken(emailConfirmation.getToken()))
                .thenReturn(Optional.empty());

        assertThrows(EmailConfirmationNotFoundException.class, () -> {

            emailConfirmationService.getUserByConfirmationToken(String.valueOf(emailConfirmation.getToken()));
        }, "Should throw EmailConfirmationNotFoundException when emailConfirmation not found by token");

        verify(userMapper, never()).toResponseDto(user);
    }


}

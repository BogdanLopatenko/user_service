package com.user_service.service.impl;

import com.user_service.constant.ExceptionConstant;
import com.user_service.dto.confirmation.UserEmailConfirmationResponseDto;
import com.user_service.dto.confirmation.UserEmailConfirmationUpdateDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.entity.User;
import com.user_service.entity.UserEmailConfirmation;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.EmailIsAlreadyActivatedException;
import com.user_service.mapper.UserEmailConfirmationMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.service.EmailConfirmationService;
import com.user_service.service.UserEmailConfirmationService;
import com.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private final UserService userService;

    private final UserEmailConfirmationService emailConfirmationService;

    private final UserMapper userMapper;

    private final UserEmailConfirmationMapper emailConfirmationMapper;

    @Value("${email-confirmation.token.expiration-durability}")
    private Short expirationDurabilityInHours;


    @Override
    public void confirmEmail(String token) {

        UserEmailConfirmationResponseDto emailConfirmation = emailConfirmationService.getByToken(UUID.fromString(token));

        log.info("Email confirmation token was found: {}", token);

        if (LocalDateTime.now().isAfter(emailConfirmation.getExpiresAt())) {

            throw new EmailConfirmationTokenExpirationException(ExceptionConstant.EMAIL_CONFIRMATION_TOKEN_HAD_BEEN_EXPIRED + emailConfirmation.getUser().getEmail());
        }

        if (emailConfirmation.getIsUsed()) {

            throw new EmailIsAlreadyActivatedException(ExceptionConstant.EMAIL_IS_ALREADY_ACTIVATED);
        }

        log.info("Tyring to update token activity");

        emailConfirmationService.update(new UserEmailConfirmationUpdateDto(emailConfirmation.getToken(), true));

        log.info("Token was updated successfully");

        log.info("Trying to update user status by ID: {}", emailConfirmation.getUser().getId());

        updateUserStatus(emailConfirmation.getUser().getId(), UserStatus.ACTIVE);

        log.info("User status was updated successfully");
    }

    @Override
    public UserResponseDto getUserByConfirmationToken(String token) {

        UserEmailConfirmationResponseDto byToken = emailConfirmationService.getByToken(UUID.fromString(token));

        return byToken.getUser();
    }


    @Override
    public UserEmailConfirmationResponseDto create(Long userId) {

        log.info("Trying to get User by id: {}", userId);

        UserResponseDto userById = userService.getById(userId);

        log.info("User was successfully found");

        User user = userMapper.toEntityFromResponseDto(userById);

        log.debug("Constructing email confirmation entity");
        UserEmailConfirmation constructedEmailConfirmation = emailConfirmationMapper.construct(user, LocalDateTime.now().plusHours(expirationDurabilityInHours), false);

        log.info("Trying to save Email confirmation entity");

        UserEmailConfirmationResponseDto createdEmailConfirmationDto = emailConfirmationService.create(constructedEmailConfirmation);

        log.info("Email confirmation was successfully saved");

        return createdEmailConfirmationDto;
    }

    private void updateUserStatus(Long userId, UserStatus userStatus) {

        userService.update(UserUpdateDto.builder()
                .id(userId)
                .status(userStatus)
                .build());
    }
}

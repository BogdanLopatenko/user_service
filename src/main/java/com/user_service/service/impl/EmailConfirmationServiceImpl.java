package com.user_service.service.impl;

import com.user_service.constant.ExceptionConstant;
import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailConfirmationNotFoundException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.mapper.EmailConfirmationMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.EmailConfirmationRepository;
import com.user_service.repository.UserRepository;
import com.user_service.service.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    @Value("${email-confirmation.token.expiration-durability}")
    private final Short expirationDurabilityInHours;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final EmailConfirmationMapper emailConfirmationMapper;

    private final EmailConfirmationRepository emailConfirmationRepository;

    private final Clock clock;

    @Override
    public EmailConfirmationResponseDto create(Long userId) {

        log.info("Trying to save email confirmation token");

        log.info("Trying to get user by ID: {}", userId);

        User userById = getUserById(userId);

        log.info("User by ID was successfully got");

        EmailConfirmation constructedEmailConfirmation = emailConfirmationMapper.construct(userById, LocalDateTime.now(clock).plusHours(expirationDurabilityInHours), false);

        log.info("Trying to save email confirmation entity");

        EmailConfirmation savedEntity = emailConfirmationRepository.save(constructedEmailConfirmation);

        log.info("Email confirmation was successfully saved: {}", savedEntity.getToken());

        return emailConfirmationMapper.toResponseDto(savedEntity);
    }

    @Override
    public void confirmEmail(String token) {

        EmailConfirmation confirmationByToken = getConfirmationByToken(UUID.fromString(token));

        Long userid = confirmationByToken.getUser().getId();

        log.info("Email confirmation token was found: {}", token);

        if (LocalDateTime.now(clock).isAfter(confirmationByToken.getExpiresAt())) {

            log.warn("Email confirmation toke has been expired at: {} hours, {} minutes", confirmationByToken.getExpiresAt().getHour(), confirmationByToken.getExpiresAt().getMinute());

            throw new EmailConfirmationTokenExpirationException(ExceptionConstant.EMAIL_CONFIRMATION_TOKEN_HAD_BEEN_EXPIRED + confirmationByToken.getUser().getEmail());
        }

        if (confirmationByToken.getIsUsed()) {

            log.warn("Email confirmation token already has been activated");

            throw new EmailAlreadyActivatedException(ExceptionConstant.EMAIL_ALREADY_ACTIVATED);
        }

        confirmationByToken.setIsUsed(true);

        log.info("Tyring to update token");

        emailConfirmationRepository.save(confirmationByToken);

        log.info("Token was updated successfully");

        log.info("Trying to update user status by ID: {}", userid);

        updateUserStatus(userid, UserStatus.ACTIVE);

        log.info("User status was updated successfully");
    }

    @Override
    public UserResponseDto getUserByConfirmationToken(String token) {

        log.info("Trying to get confirmation entity by token: {}", token);

        EmailConfirmation confirmationByToken = getConfirmationByToken(UUID.fromString(token));

        User userFromToken = confirmationByToken.getUser();

        return userMapper.toResponseDto(userFromToken);
    }

    private EmailConfirmation getConfirmationByToken(UUID token) {

        log.info("Entering findByToken(UUID token) method");

        log.info("Tying to get entity by UUID token: {}", token);

        EmailConfirmation emailConfirmation = emailConfirmationRepository.findById(token).orElseThrow(() ->
                new EmailConfirmationNotFoundException(ExceptionConstant.EMAIL_CONFIRMATION_NOT_FOUND_BY_TOKEN + token));

        log.info("Exit findByToken(UUID token) method");
        return emailConfirmation;
    }

    private void updateUserStatus(Long userId, UserStatus userStatus) {

        log.info("Entering updateUserStatus(Long userId, UserStatus userStatus) method");

        User userById = getUserById(userId);

        userById.setStatus(userStatus);

        log.info("Trying to save user");

        userRepository.save(userById);

        log.info("Exit updateUserStatus(Long userId, UserStatus userStatus) method");
    }

    private User getUserById(Long userId) {
        log.info("Entering getUserById(Long userId) method");

        log.info("Trying to get user by ID: {}", userId);

        User userById = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(ExceptionConstant.USER_NOT_FOUND_BY_ID + userId));

        log.info("Exit getUserById(Long userId) method");

        return userById;
    }
}

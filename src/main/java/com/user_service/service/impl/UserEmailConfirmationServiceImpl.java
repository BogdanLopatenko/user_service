package com.user_service.service.impl;

import com.user_service.constant.ExceptionConstant;
import com.user_service.dto.confirmation.UserEmailConfirmationUpdateDto;
import com.user_service.dto.confirmation.UserEmailConformationResponseDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.entity.User;
import com.user_service.entity.UserEmailConfirmation;
import com.user_service.exception.UserEmailConfirmationNotFoundException;
import com.user_service.mapper.UserEmailConfirmationMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.UserEmailConfirmationRepository;
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
public class UserEmailConfirmationServiceImpl implements UserEmailConfirmationService {

    private final UserEmailConfirmationRepository userEmailConfirmationRepository;

    private final UserService userService;

    private final UserMapper userMapper;

    private final UserEmailConfirmationMapper userEmailConfirmationMapper;

    @Value("${email-confirmation.token.expiration-durability}")
    private Short expirationDurabilityInHours;

    @Override
    public UserEmailConformationResponseDto getByToken(UUID token) {

        UserEmailConfirmation userEmailConfirmationByToken = findByToken(token);

        log.info("Email confirmation entity was successfully found");

        return userEmailConfirmationMapper.toResponseDto(userEmailConfirmationByToken);
    }

    @Override
    public UserEmailConformationResponseDto create(Long userId) {


        log.info("Trying to get User by id: {}", userId);

        UserResponseDto userById = userService.getById(userId);

        log.info("User was successfully found");

        User user = userMapper.toEntityFromResponseDto(userById);

        log.debug("Constructing email confirmation entity");
        UserEmailConfirmation constructedEmailConfiguration = userEmailConfirmationMapper.construct(user, LocalDateTime.now().plusHours(expirationDurabilityInHours), false);

        log.info("Trying to save Email confirmation entity");

        UserEmailConfirmation savedConfirmation = userEmailConfirmationRepository.save(constructedEmailConfiguration);

        log.info("Email confirmation was successfully saved");

        return userEmailConfirmationMapper.toResponseDto(savedConfirmation);
    }

    @Override
    public void updateIsUsedToken(UserEmailConfirmationUpdateDto dto) {

        UserEmailConfirmation confirmationByToken = findByToken(dto.getToken());

        log.info("Email confirmation entity was found");

        confirmationByToken.setIsUsed(true);

        log.info("Trying to save email confirmation token");

        userEmailConfirmationRepository.save(confirmationByToken);

        log.info("Email confirmation token was successfully found");
    }

    private UserEmailConfirmation findByToken(UUID token){

        log.info("Tying to get entity by UUID token: {}", token);

        return userEmailConfirmationRepository.findById(token).orElseThrow(() -> new UserEmailConfirmationNotFoundException(ExceptionConstant.EMAIL_CONFIRMATION_NOT_FOUND_BY_TOKEN + token));
    }
}

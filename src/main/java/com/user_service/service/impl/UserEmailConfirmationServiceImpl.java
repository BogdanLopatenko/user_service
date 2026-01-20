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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        return userEmailConfirmationMapper.toResponseDto(userEmailConfirmationByToken);
    }

    @Override
    public UserEmailConformationResponseDto create(Long userId) {

        UserResponseDto userById = userService.getById(userId);
        User user = userMapper.toEntityFromResponseDto(userById);

        UserEmailConfirmation constructedEmailConfiguration = userEmailConfirmationMapper.construct(user, LocalDateTime.now().plusHours(expirationDurabilityInHours), false);

        UserEmailConfirmation savedConfirmation = userEmailConfirmationRepository.save(constructedEmailConfiguration);

        return userEmailConfirmationMapper.toResponseDto(savedConfirmation);
    }

    @Override
    public void updateIsUsedToken(UserEmailConfirmationUpdateDto dto) {

        UserEmailConfirmation confirmationByToken = findByToken(dto.getToken());

        confirmationByToken.setIsUsed(true);

        userEmailConfirmationRepository.save(confirmationByToken);
    }

    private UserEmailConfirmation findByToken(UUID token){
        return userEmailConfirmationRepository.findById(token).orElseThrow(() -> new UserEmailConfirmationNotFoundException(ExceptionConstant.EMAIL_CONFIRMATION_NOT_FOUND_BY_TOKEN + token));
    }
}

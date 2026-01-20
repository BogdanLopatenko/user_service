package com.user_service.service;

import com.user_service.dto.confirmation.UserEmailConfirmationUpdateDto;
import com.user_service.dto.confirmation.UserEmailConformationResponseDto;

import java.util.UUID;

public interface UserEmailConfirmationService {

    UserEmailConformationResponseDto getByToken(UUID token);

    UserEmailConformationResponseDto create(Long userId);

    void updateIsUsedToken(UserEmailConfirmationUpdateDto dto);
}

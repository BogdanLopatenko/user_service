package com.user_service.service;

import com.user_service.dto.confirmation.UserEmailConfirmationResponseDto;
import com.user_service.dto.user.UserResponseDto;

public interface EmailConfirmationService {

    void confirmEmail(String token);

    UserResponseDto getUserByConfirmationToken(String token);

    UserEmailConfirmationResponseDto create(Long userId);
}

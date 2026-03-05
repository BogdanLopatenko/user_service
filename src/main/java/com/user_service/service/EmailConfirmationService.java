package com.user_service.service;

import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserResponseDto;

public interface EmailConfirmationService {

    EmailConfirmationResponseDto create(Long userId);

    void confirmEmail(String token);

    UserResponseDto getUserByConfirmationToken(String token);
}

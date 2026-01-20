package com.user_service.dto.confirmation;

import com.user_service.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailConformationResponseDto {

    private UUID token;

    private UserResponseDto user;

    private LocalDateTime expiresAt;

    private Boolean isUsed;
}

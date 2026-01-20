package com.user_service.dto.confirmation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailConfirmationRequestDto {

    @NotNull(message = "User id can't be null.")
    private Long userId;

    @NotNull(message = "Expiration date can't be null.")
    @FutureOrPresent(message = "Expiration date can't be in past.")
    private LocalDateTime expiresAt;

    @NotNull(message = "Is used can't be null.")
    private Boolean isUsed;
}

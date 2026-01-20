package com.user_service.dto.confirmation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailConfirmationUpdateDto {

    @NotNull(message = "Token can't be null")
    private UUID token;

    @NotNull(message = "Is used can't be null.")
    private Boolean isUsed;
}

package com.user_service.dto.confirmation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfirmationUpdateDto {


    @NotNull(message = "Is used can't be null.")
    private Boolean isUsed;
}

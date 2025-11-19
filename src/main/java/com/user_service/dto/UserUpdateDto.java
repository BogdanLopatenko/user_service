package com.user_service.dto;

import com.user_service.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotNull(message = "Id can't be null.")
    @Positive(message = "Id must be a positive number only.")
    private Long id;

    @Size(message = "Username must be in range > 4 & < 75", min = 4, max = 75)
    private String username;

    @Size(message = "Password must be in range > 4 & < 50", min = 4, max = 50)
    private String password;

    @Size(message = "Firstname must be in range > 2 & < 50", min = 2, max = 50)
    private String firstname;

    @Size(message = "Lastname must be in range > 2 & < 50", min = 2, max = 50)
    private String lastname;

    @Email(message = "Invalid email format.")
    @Size(message = "Email must be in range > 5 & < 100", min = 5, max = 100)
    private String email;

    private UserRole role;

    private Boolean isActive;
}

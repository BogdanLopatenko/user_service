package com.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Username can't be null or blank.")
    @Size(message = "Username must be in range > 4 & < 75", min = 4, max = 75)
    private String username;

    @NotBlank(message = "Password can't be null or blank.")
    @Size(message = "Password must be in range > 4 & < 50", min = 4, max = 50)
    private String password;

    @Size(message = "Firstname must be in range > 2 & < 50", min = 2, max = 50)
    private String firstname;

    @Size(message = "Lastname must be in range > 2 & < 50", min = 2, max = 50)
    private String lastname;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email can't be null or blank.")
    @Size(message = "Email must be in range > 5 & < 100", min = 5, max = 100)
    private String email;
}

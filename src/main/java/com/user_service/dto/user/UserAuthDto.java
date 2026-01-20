package com.user_service.dto.user;

import com.user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDto {

    private String username;

    private String password;

    private String email;

    private UserRole role;
}

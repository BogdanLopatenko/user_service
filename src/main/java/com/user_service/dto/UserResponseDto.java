package com.user_service.dto;

import com.user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String username;

    private String firstname;

    private String lastname;

    private String email;

    private UserRole role;

    private Boolean isActive;
}

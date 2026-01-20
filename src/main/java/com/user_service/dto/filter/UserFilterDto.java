package com.user_service.dto.filter;

import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {

    private String username;

    private String firstname;

    private String lastname;

    private String email;

    private UserRole role;

    private UserStatus status;
}

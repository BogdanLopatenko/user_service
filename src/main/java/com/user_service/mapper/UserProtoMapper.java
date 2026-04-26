package com.user_service.mapper;

import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.generated.UserRole;
import com.user_service.generated.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserProtoMapper {

    public com.user_service.generated.UserAuthDto toProtoAuthDto(UserAuthDto dto) {

        return com.user_service.generated.UserAuthDto.newBuilder()
                .setUsername(dto.getUsername())
                .setPassword(dto.getPassword())
                .setEmail(dto.getEmail())
                .setRole(toProtoUserRole(dto.getRole()))
                .build();
    }

    public com.user_service.generated.UserResponseDto toProtoResponseDto(UserResponseDto dto) {

        return com.user_service.generated.UserResponseDto.newBuilder()
                .setId(dto.getId())
                .setUsername(dto.getUsername())
                .setFirstname(dto.getFirstname())
                .setLastname(dto.getLastname())
                .setEmail(dto.getEmail())
                .setRole(toProtoUserRole(dto.getRole()))
                .setStatus(toProtoUserStatus(dto.getStatus()))
                .build();
    }

    public UserRequestDto toRequestDtoFromProtoDto(com.user_service.generated.UserRequestDto dto) {

        return new UserRequestDto(
                dto.getUsername(),
                dto.getPassword(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEmail());
    }

    private UserRole toProtoUserRole(com.user_service.enums.UserRole role) {

        if (role == null) {

            throw new RuntimeException();//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }

        return switch (role) {
            case USER -> UserRole.USER;
            case ADMIN -> UserRole.ADMIN;
        };
    }

    private UserStatus toProtoUserStatus(com.user_service.enums.UserStatus status) {

        if (status == null) {

            throw new RuntimeException();//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }

        return switch (status) {
            case ACTIVE -> UserStatus.ACTIVE;
            case DISABLED -> UserStatus.DISABLED;
            case NEED_EMAIL_CONFIRMATION -> UserStatus.NEED_EMAIL_CONFIRMATION;
        };
    }
}

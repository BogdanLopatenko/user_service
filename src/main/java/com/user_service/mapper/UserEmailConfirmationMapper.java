package com.user_service.mapper;

import com.user_service.dto.confirmation.UserEmailConfirmationRequestDto;
import com.user_service.dto.confirmation.UserEmailConfirmationUpdateDto;
import com.user_service.dto.confirmation.UserEmailConformationResponseDto;
import com.user_service.entity.User;
import com.user_service.entity.UserEmailConfirmation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = UserMapper.class
        )
public interface UserEmailConfirmationMapper {

        UserEmailConformationResponseDto toResponseDto(UserEmailConfirmation entity);

        UserEmailConfirmation toEntityFromRequestDto(UserEmailConfirmationRequestDto dto);

        @Mapping(target = "token", ignore = true)
        UserEmailConfirmation construct(User user, LocalDateTime expiresAt, Boolean isUsed);

        void updateFromDb(UserEmailConfirmationUpdateDto dto, @MappingTarget UserEmailConfirmation entity);
}

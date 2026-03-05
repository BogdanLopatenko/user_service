package com.user_service.mapper;

import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = UserMapper.class
        )
public interface EmailConfirmationMapper {

    EmailConfirmationResponseDto toResponseDto(EmailConfirmation entity);

    @Mapping(target = "token", ignore = true)
    EmailConfirmation construct(User user, LocalDateTime expiresAt, Boolean isUsed);
}

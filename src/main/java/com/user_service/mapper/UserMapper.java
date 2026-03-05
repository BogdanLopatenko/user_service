package com.user_service.mapper;

import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR
        )
public interface UserMapper {

    UserResponseDto toResponseDto(User user);

    UserAuthDto toAuthDto(User user);

    @Mapping(target = "password", ignore = true)
    User toEntityFromResponseDto(UserResponseDto userResponseDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntityFromRequestDto(UserRequestDto dto);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromDb(UserUpdateDto dto, @MappingTarget User user);

}

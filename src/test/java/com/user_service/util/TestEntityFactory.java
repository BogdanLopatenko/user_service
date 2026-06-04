package com.user_service.util;

import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.util.builder.EmailConfirmationTestBuilder;
import com.user_service.util.builder.UserTestBuilder;

public class TestEntityFactory {

    private static final UserTestBuilder userTestBuilder = new UserTestBuilder();

    private static final EmailConfirmationTestBuilder emailConfirmationTestBuilder = new EmailConfirmationTestBuilder();


    public static User initUser() {
        return userTestBuilder.build();
    }

    public static UserAuthDto initUserAuthDto() {
        return userTestBuilder.buildAuthDto();
    }

    public static UserResponseDto initUserResponseDto() {
        return userTestBuilder.buildResponseDto();
    }

    public static UserRequestDto initUserRequestDto() {
        return userTestBuilder.buildRequestDto();
    }

    public static UserUpdateDto initUserUpdateDto() {
        return userTestBuilder.buildUpdateDto();
    }

    public static com.user_service.generated.UserAuthDto initUserProtoAuthDto() {
        return userTestBuilder.buildProtoAuthDto();
    }

    public static com.user_service.generated.UserRequestDto initUserProtoRequestDto() {
        return userTestBuilder.buildProtoRequestDto();
    }

    public static com.user_service.generated.UserResponseDto initUserProtoResponseDto() {
        return userTestBuilder.buildProtoResponseDto();
    }

    public static com.user_service.generated.UserId initUserId() {
        return userTestBuilder.buildUserId();
    }

    public static EmailConfirmationResponseDto initEmailConfirmationResponseDto() {
        return emailConfirmationTestBuilder.buildResponseDto();
    }

    public static EmailConfirmation initEmailConfirmation() {
        return emailConfirmationTestBuilder.build();
    }


}

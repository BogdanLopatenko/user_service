package com.user_service.util.builder;

import com.user_service.constant.TestConstant;
import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class EmailConfirmationTestBuilder {

    private UUID token = UUID.fromString("22257367-58ce-41e9-ac30-b57338fd70dc");

    private User user = new UserTestBuilder().build();

    private LocalDateTime expiresAt = LocalDateTime.ofInstant(TestConstant.INSTANCE_AFTER, ZoneOffset.UTC);
    private Boolean isUsed = false;

    public EmailConfirmationTestBuilder withToken(UUID token) {

        this.token = token;
        return this;
    }

    public EmailConfirmationTestBuilder withUser(User user) {

        this.user = user;
        return this;
    }


    public EmailConfirmationTestBuilder withExpiresAtInstant(Instant expiresAt) {

        this.expiresAt = LocalDateTime.ofInstant(expiresAt, ZoneOffset.UTC);
        return this;
    }

    public EmailConfirmationTestBuilder withExpiresAt(LocalDateTime expiresAt) {

        this.expiresAt = expiresAt;
        return this;
    }

    public EmailConfirmationTestBuilder withUsed(Boolean isUsed) {

        this.isUsed = isUsed;
        return this;
    }

    public EmailConfirmation build() {

        return new EmailConfirmation(
                token,
                user,
                expiresAt,
                isUsed
        );
    }

    public EmailConfirmationResponseDto buildResponseDto() {

        return new EmailConfirmationResponseDto(
                token,
                new UserTestBuilder().buildResponseDto(),
                expiresAt,
                isUsed
        );
    }
}

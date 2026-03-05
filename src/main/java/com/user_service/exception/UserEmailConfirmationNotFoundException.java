package com.user_service.exception;

public class UserEmailConfirmationNotFoundException extends RuntimeException {
    public UserEmailConfirmationNotFoundException(String message) {
        super(message);
    }
}

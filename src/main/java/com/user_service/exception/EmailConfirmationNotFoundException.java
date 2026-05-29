package com.user_service.exception;

public class EmailConfirmationNotFoundException extends RuntimeException {
    public EmailConfirmationNotFoundException(String message) {
        super(message);
    }
}

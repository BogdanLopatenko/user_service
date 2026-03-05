package com.user_service.exception;

public class EmailConfirmationTokenExpirationException extends RuntimeException {
    public EmailConfirmationTokenExpirationException(String message) {
        super(message);
    }
}

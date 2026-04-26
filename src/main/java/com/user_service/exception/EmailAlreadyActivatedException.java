package com.user_service.exception;

public class EmailAlreadyActivatedException extends RuntimeException {
    public EmailAlreadyActivatedException(String message) {
        super(message);
    }
}

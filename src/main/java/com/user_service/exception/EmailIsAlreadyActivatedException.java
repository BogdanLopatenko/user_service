package com.user_service.exception;

public class EmailIsAlreadyActivatedException extends RuntimeException {
    public EmailIsAlreadyActivatedException(String message) {
        super(message);
    }
}

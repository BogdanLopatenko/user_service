package com.user_service.exception;

public class EmailIsAlreadyExistException extends RuntimeException {
    public EmailIsAlreadyExistException(String message) {
        super(message);
    }
}

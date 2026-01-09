package com.user_service.exception;

public class UsernameIsAlreadyExistException extends RuntimeException {
    public UsernameIsAlreadyExistException(String message) {
        super(message);
    }
}

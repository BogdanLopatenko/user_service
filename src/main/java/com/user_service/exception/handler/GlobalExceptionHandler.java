package com.user_service.exception.handler;

import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(EmailIsAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailIsAlreadyExistException(EmailIsAlreadyExistException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UsernameIsAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleUsernameIsAlreadyExistException(UsernameIsAlreadyExistException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }
}

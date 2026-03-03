package com.user_service.exception.handler;

import com.user_service.dto.ExceptionResponseDto;
import com.user_service.enums.ExceptionStatus;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.EmailIsAlreadyActivatedException;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserEmailConfirmationNotFoundException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotFoundException(UserNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponseDto(ExceptionStatus.USER_NOT_FOUND.name(), exception.getMessage()));
    }

    @ExceptionHandler(EmailIsAlreadyExistException.class)
    public ResponseEntity<ExceptionResponseDto> handleEmailIsAlreadyExistException(EmailIsAlreadyExistException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponseDto(ExceptionStatus.EMAIL_IS_ALREADY_EXIST.name(), exception.getMessage()));
    }

    @ExceptionHandler(UsernameIsAlreadyExistException.class)
    public ResponseEntity<ExceptionResponseDto> handleUsernameIsAlreadyExistException(UsernameIsAlreadyExistException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponseDto(ExceptionStatus.USERNAME_IS_ALREADY_EXIST.name(), exception.getMessage()));
    }

    @ExceptionHandler(UserEmailConfirmationNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserEmailConfirmationNotFoundException(UserEmailConfirmationNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponseDto(ExceptionStatus.USER_EMAIL_CONFIRMATION_NOT_FOUND.name(), exception.getMessage()));
    }

    @ExceptionHandler(EmailConfirmationTokenExpirationException.class)
    public ResponseEntity<ExceptionResponseDto> handleEmailConfirmationTokenExpirationException(EmailConfirmationTokenExpirationException exception) {

        return ResponseEntity.status(HttpStatus.GONE).body(new ExceptionResponseDto(ExceptionStatus.USER_EMAIL_CONFIRMATION_TOKEN_EXPIRED.name(), exception.getMessage()));
    }

    @ExceptionHandler(EmailIsAlreadyActivatedException.class)
    public ResponseEntity<ExceptionResponseDto> handleEmailIsAlreadyActivatedException(EmailIsAlreadyActivatedException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionResponseDto(ExceptionStatus.EMAIL_IS_ALREADY_ACTIVATED.name(), exception.getMessage()));
    }


}

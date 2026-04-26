package com.user_service.exception.handler;

import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserEmailConfirmationNotFoundException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFoundException(UserNotFoundException exception) {

        return Status.NOT_FOUND
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(EmailIsAlreadyExistException.class)
    public StatusRuntimeException handleEmailIsAlreadyExistException(EmailIsAlreadyExistException exception) {

        return Status.ALREADY_EXISTS
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(UsernameIsAlreadyExistException.class)
    public StatusRuntimeException handleUsernameIsAlreadyExistException(UsernameIsAlreadyExistException exception) {

        return Status.ALREADY_EXISTS
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(UserEmailConfirmationNotFoundException.class)
    public StatusRuntimeException handleUserEmailConfirmationNotFoundException(UserEmailConfirmationNotFoundException exception) {

        return Status.NOT_FOUND
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(EmailConfirmationTokenExpirationException.class)
    public StatusRuntimeException handleEmailConfirmationTokenExpirationException(EmailConfirmationTokenExpirationException exception) {

        return Status.UNAUTHENTICATED
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(EmailAlreadyActivatedException.class)
    public StatusRuntimeException handleEmailIsAlreadyActivatedException(EmailAlreadyActivatedException exception) {

        return Status.ALREADY_EXISTS
                .withDescription(exception.getMessage())
                .asRuntimeException();
    }


}

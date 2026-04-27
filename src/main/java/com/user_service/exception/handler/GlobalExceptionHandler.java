package com.user_service.exception.handler;

import com.google.protobuf.Any;
import com.user_service.enums.ExceptionStatus;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailAlreadyExistException;
import com.user_service.exception.UserEmailConfirmationNotFoundException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameAlreadyExistException;
import com.user_service.generated.ErrorInfo;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFoundException(UserNotFoundException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.USER_NOT_FOUND.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(io.grpc.Status.NOT_FOUND.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public StatusRuntimeException handleEmailIsAlreadyExistException(EmailAlreadyExistException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.EMAIL_ALREADY_EXIST.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Status.ALREADY_EXISTS.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public StatusRuntimeException handleUsernameIsAlreadyExistException(UsernameAlreadyExistException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.USERNAME_ALREADY_EXIST.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Status.ALREADY_EXISTS.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    @ExceptionHandler(UserEmailConfirmationNotFoundException.class)
    public StatusRuntimeException handleUserEmailConfirmationNotFoundException(UserEmailConfirmationNotFoundException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.USER_EMAIL_CONFIRMATION_NOT_FOUND.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Status.NOT_FOUND.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    @ExceptionHandler(EmailConfirmationTokenExpirationException.class)
    public StatusRuntimeException handleEmailConfirmationTokenExpirationException(EmailConfirmationTokenExpirationException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.USER_EMAIL_CONFIRMATION_TOKEN_EXPIRED.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Status.UNAUTHENTICATED.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }

    @ExceptionHandler(EmailAlreadyActivatedException.class)
    public StatusRuntimeException handleEmailIsAlreadyActivatedException(EmailAlreadyActivatedException exception) {

        ErrorInfo errorInfo = ErrorInfo.newBuilder()
                .setCode(ExceptionStatus.EMAIL_ALREADY_ACTIVATED.name())
                .setMessage(exception.getMessage())
                .build();

        com.google.rpc.Status status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Status.ALREADY_EXISTS.getCode().value())
                        .addDetails(Any.pack(errorInfo))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }


}

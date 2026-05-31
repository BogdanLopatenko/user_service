package com.user_service.grpc;

import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailAlreadyExistException;
import com.user_service.exception.EmailConfirmationNotFoundException;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameAlreadyExistException;
import io.grpc.Status;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GrpcExceptionMapper {

    private static final Map<Class<? extends Throwable>, Status.Code> EXCEPTION_MAP =
            Map.of(
                    UserNotFoundException.class, Status.Code.NOT_FOUND,
                    EmailAlreadyExistException.class, Status.Code.ALREADY_EXISTS,
                    UsernameAlreadyExistException.class, Status.Code.ALREADY_EXISTS,
                    EmailConfirmationNotFoundException.class, Status.Code.NOT_FOUND,
                    EmailConfirmationTokenExpirationException.class, Status.Code.UNAUTHENTICATED,
                    EmailAlreadyActivatedException.class, Status.Code.ALREADY_EXISTS
            );

    public Status.Code getStatusCode(Throwable throwable){

        return EXCEPTION_MAP.getOrDefault(
                throwable.getClass(),
                Status.Code.INTERNAL
        );
    }
}

package com.user_service.unit;

import com.google.protobuf.Empty;
import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.enums.UserRole;
import com.user_service.exception.*;
import com.user_service.generated.ConfirmationToken;
import com.user_service.generated.UserId;
import com.user_service.generated.UserResponseDto;
import com.user_service.generated.Username;
import com.user_service.grpc.GrpcUserServiceImpl;
import com.user_service.mapper.UserProtoMapper;
import com.user_service.service.UserService;
import com.user_service.service.impl.EmailConfirmationServiceImpl;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.user_service.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GrpcUserServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProtoMapper userProtoMapper;

    @Mock
    private EmailConfirmationServiceImpl emailConfirmationService;

    @Mock
    private StreamObserver<com.user_service.generated.UserAuthDto> authResponseObserver;

    @Mock
    private StreamObserver<UserResponseDto> responseObserver;

    @Mock
    private StreamObserver<ConfirmationToken> tokenResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private GrpcUserServiceImpl grpcService;


    @Test
    void shouldSendAuthDtoWhenUserExistsByUsername() {

        UserAuthDto userAuthDto = initUserAuthDto();

        com.user_service.generated.UserAuthDto protoAuthDto =
                initUserProtoAuthDto();

        Username username = Username.newBuilder()
                .setUsername(userAuthDto.getUsername())
                .build();

        when(userService.getByUsername(userAuthDto.getUsername()))
                .thenReturn(userAuthDto);

        when(userProtoMapper.toProtoAuthDto(userAuthDto))
                .thenReturn(protoAuthDto);

        grpcService.getByUsername(username, authResponseObserver);

        verify(authResponseObserver).onNext(protoAuthDto);
        verify(authResponseObserver).onCompleted();
        verify(authResponseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExistByUsername() {

        Username username = Username.newBuilder()
                .setUsername("test")
                .build();

        when(userService.getByUsername(anyString()))
                .thenThrow(new UserNotFoundException("User not found by username"));

        assertThrows(UserNotFoundException.class, () ->
                grpcService.getByUsername(username, authResponseObserver));

        verify(userProtoMapper, never()).toProtoAuthDto(any());
        verify(authResponseObserver, never()).onNext(any());
        verify(authResponseObserver, never()).onCompleted();
        verify(authResponseObserver, never()).onError(any());
    }

    @Test
    void shouldSendUserResponseDtoWhenUserExistsByConfirmationToken() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken("token")
                .build();

        com.user_service.dto.user.UserResponseDto userResponseDto =
                initUserResponseDto();

        UserResponseDto protoResponseDto =
                initUserProtoResponseDto();

        when(emailConfirmationService.getUserByConfirmationToken(
                confirmationToken.getToken()))
                .thenReturn(userResponseDto);

        when(userProtoMapper.toProtoResponseDto(userResponseDto))
                .thenReturn(protoResponseDto);

        grpcService.getUserByConfirmationToken(
                confirmationToken,
                responseObserver
        );

        verify(responseObserver).onNext(protoResponseDto);
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowEmailConfirmationNotFoundExceptionWhenConfirmationTokenDoesNotExist() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken("invalid")
                .build();

        when(emailConfirmationService.getUserByConfirmationToken(anyString()))
                .thenThrow(new EmailConfirmationNotFoundException("Token not found"));

        assertThrows(EmailConfirmationNotFoundException.class, () ->
                grpcService.getUserByConfirmationToken(
                        confirmationToken,
                        responseObserver
                ));

        verify(userProtoMapper, never()).toProtoResponseDto(any());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void shouldSendResponseDtoWhenUserIsCreatedSuccessfully() {

        com.user_service.generated.UserRequestDto protoRequestDto =
                initUserProtoRequestDto();

        UserRequestDto requestDto =
                initUserRequestDto();

        com.user_service.dto.user.UserResponseDto responseDto =
                initUserResponseDto();

        UserResponseDto protoResponseDto =
                initUserProtoResponseDto();

        when(userProtoMapper.toRequestDtoFromProtoDto(protoRequestDto))
                .thenReturn(requestDto);

        when(userService.createWithRole(requestDto, UserRole.USER))
                .thenReturn(responseDto);

        when(userProtoMapper.toProtoResponseDto(responseDto))
                .thenReturn(protoResponseDto);

        grpcService.create(protoRequestDto, responseObserver);

        verify(responseObserver).onNext(protoResponseDto);
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowEmailAlreadyExistExceptionWhenEmailAlreadyExistsDuringCreate() {

        com.user_service.generated.UserRequestDto protoRequestDto =
                initUserProtoRequestDto();

        UserRequestDto requestDto =
                initUserRequestDto();

        when(userProtoMapper.toRequestDtoFromProtoDto(protoRequestDto))
                .thenReturn(requestDto);

        when(userService.createWithRole(requestDto, UserRole.USER))
                .thenThrow(new EmailAlreadyExistException("Email already exist"));

        assertThrows(EmailAlreadyExistException.class, () ->
                grpcService.create(protoRequestDto, responseObserver));

        verify(userProtoMapper, never()).toProtoResponseDto(any());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowUsernameAlreadyExistExceptionWhenUsernameAlreadyExistsDuringCreate() {

        com.user_service.generated.UserRequestDto protoRequestDto =
                initUserProtoRequestDto();

        UserRequestDto requestDto =
                initUserRequestDto();

        when(userProtoMapper.toRequestDtoFromProtoDto(protoRequestDto))
                .thenReturn(requestDto);

        when(userService.createWithRole(requestDto, UserRole.USER))
                .thenThrow(new UsernameAlreadyExistException("Username already exist"));

        assertThrows(UsernameAlreadyExistException.class, () ->
                grpcService.create(protoRequestDto, responseObserver));

        verify(userProtoMapper, never()).toProtoResponseDto(any());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void shouldSendConfirmationTokenWhenEmailConfirmationTokenIsGenerated() {

        UserId userId = initUserId();

        EmailConfirmationResponseDto emailConfirmation =
                initEmailConfirmationResponseDto();

        when(emailConfirmationService.create(userId.getId()))
                .thenReturn(emailConfirmation);

        grpcService.generateEmailConfirmationToken(
                userId,
                tokenResponseObserver
        );

        ConfirmationToken expected = ConfirmationToken.newBuilder()
                .setToken(String.valueOf(emailConfirmation.getToken()))
                .build();

        verify(emailConfirmationService).create(userId.getId());
        verify(tokenResponseObserver).onNext(expected);
        verify(tokenResponseObserver).onCompleted();
        verify(tokenResponseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGeneratingEmailConfirmationTokenForNonExistingUser() {

        UserId userId = initUserId();

        when(emailConfirmationService.create(userId.getId()))
                .thenThrow(new UserNotFoundException("User not found by id"));

        assertThrows(UserNotFoundException.class, () ->
                grpcService.generateEmailConfirmationToken(
                        userId,
                        tokenResponseObserver
                ));

        verify(tokenResponseObserver, never()).onNext(any());
        verify(tokenResponseObserver, never()).onCompleted();
        verify(tokenResponseObserver, never()).onError(any());
    }

    @Test
    void shouldConfirmUserEmailAndSendEmptyResponseWhenTokenIsValid() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken("token")
                .build();

        grpcService.confirmUserEmail(
                confirmationToken,
                emptyResponseObserver
        );

        verify(emailConfirmationService)
                .confirmEmail(confirmationToken.getToken());

        verify(emptyResponseObserver)
                .onNext(Empty.getDefaultInstance());

        verify(emptyResponseObserver)
                .onCompleted();

        verify(emptyResponseObserver, never())
                .onError(any());
    }

    @Test
    void shouldThrowEmailConfirmationTokenExpirationExceptionWhenEmailConfirmationTokenIsExpired() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken("expired")
                .build();

        doThrow(new EmailConfirmationTokenExpirationException(
                "Token has been expired"))
                .when(emailConfirmationService)
                .confirmEmail(anyString());

        assertThrows(
                EmailConfirmationTokenExpirationException.class,
                () -> grpcService.confirmUserEmail(
                        confirmationToken,
                        emptyResponseObserver
                )
        );

        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();
        verify(emptyResponseObserver, never()).onError(any());
    }

    @Test
    void shouldThrowEmailAlreadyActivatedExceptionWhenEmailIsAlreadyActivated() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken("activated")
                .build();

        doThrow(new EmailAlreadyActivatedException(
                "Email already activated"))
                .when(emailConfirmationService)
                .confirmEmail(anyString());

        assertThrows(
                EmailAlreadyActivatedException.class,
                () -> grpcService.confirmUserEmail(
                        confirmationToken,
                        emptyResponseObserver
                )
        );

        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();
        verify(emptyResponseObserver, never()).onError(any());
    }
}

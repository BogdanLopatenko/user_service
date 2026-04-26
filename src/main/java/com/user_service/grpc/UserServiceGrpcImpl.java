package com.user_service.grpc;

import com.google.protobuf.Empty;
import com.user_service.enums.UserRole;
import com.user_service.generated.ConfirmationToken;
import com.user_service.generated.UserAuthDto;
import com.user_service.generated.UserId;
import com.user_service.generated.UserRequestDto;
import com.user_service.generated.UserResponseDto;
import com.user_service.generated.Username;
import com.user_service.mapper.UserProtoMapper;
import com.user_service.service.EmailConfirmationService;
import com.user_service.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends com.user_service.generated.UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    private final UserProtoMapper userMapper;

    private final EmailConfirmationService emailConfirmationService;

    @Override
    public void getByUsername(Username request, StreamObserver<UserAuthDto> responseObserver) {

        String username = request.getUsername();

        com.user_service.dto.user.UserAuthDto byUsername = userService.getByUsername(username);

        UserAuthDto authDto = userMapper.toProtoAuthDto(byUsername);

        responseObserver.onNext(authDto);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByConfirmationToken(ConfirmationToken request, StreamObserver<UserResponseDto> responseObserver) {

        String confirmationToken = request.getToken();

        com.user_service.dto.user.UserResponseDto userByConfirmationToken = emailConfirmationService.getUserByConfirmationToken(confirmationToken);

        UserResponseDto responseDto = userMapper.toProtoResponseDto(userByConfirmationToken);

        responseObserver.onNext(responseDto);
        responseObserver.onCompleted();
    }

    @Override
    public void create(UserRequestDto request, StreamObserver<UserResponseDto> responseObserver) {

        com.user_service.dto.user.UserRequestDto userRequestDto = userMapper.toRequestDtoFromProtoDto(request);

        com.user_service.dto.user.UserResponseDto createdUser = userService.createWithRole(userRequestDto, UserRole.USER);

        UserResponseDto grpcResponseDto = userMapper.toProtoResponseDto(createdUser);

        responseObserver.onNext(grpcResponseDto);
        responseObserver.onCompleted();
    }

    @Override
    public void generateEmailVerificationToken(UserId request, StreamObserver<ConfirmationToken> responseObserver) {

        UUID token = emailConfirmationService.create(request.getId()).getToken();

        String tokenAsString = String.valueOf(token);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(tokenAsString).build();

        responseObserver.onNext(confirmationToken);
        responseObserver.onCompleted();
    }

    @Override
    public void verifyUserEmail(ConfirmationToken request, StreamObserver<Empty> responseObserver) {

        emailConfirmationService.confirmEmail(request.getToken());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

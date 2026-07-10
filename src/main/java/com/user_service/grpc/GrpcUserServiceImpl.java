package com.user_service.grpc;

import com.google.protobuf.Empty;
import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.enums.UserRole;
import com.user_service.generated.*;
import com.user_service.mapper.UserProtoMapper;
import com.user_service.service.EmailConfirmationService;
import com.user_service.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserServiceImpl extends com.user_service.generated.UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    private final UserProtoMapper userProtoMapper;

    private final EmailConfirmationService emailConfirmationService;

    @Override
    public void getByUsername(Username request, StreamObserver<UserAuthDto> responseObserver) {

        String username = request.getUsername();

        com.user_service.dto.user.UserAuthDto byUsername = userService.getByUsername(username);

        UserAuthDto authDto = userProtoMapper.toProtoAuthDto(byUsername);

        responseObserver.onNext(authDto);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByConfirmationToken(ConfirmationToken request,
                                           StreamObserver<UserResponseDto> responseObserver) {

        String confirmationToken = request.getToken();

        com.user_service.dto.user.UserResponseDto userByConfirmationToken =
                emailConfirmationService.getUserByConfirmationToken(confirmationToken);

        UserResponseDto responseDto = userProtoMapper.toProtoResponseDto(userByConfirmationToken);

        responseObserver.onNext(responseDto);
        responseObserver.onCompleted();
    }

    @Override
    public void create(UserRequestDto request, StreamObserver<UserResponseDto> responseObserver) {

        com.user_service.dto.user.UserRequestDto userRequestDto = userProtoMapper.toRequestDtoFromProtoDto(request);

        com.user_service.dto.user.UserResponseDto createdUser = userService.createWithRole(userRequestDto, UserRole.USER);

        UserResponseDto grpcResponseDto = userProtoMapper.toProtoResponseDto(createdUser);

        responseObserver.onNext(grpcResponseDto);
        responseObserver.onCompleted();
    }

    @Override
    public void generateEmailConfirmationToken(UserId userid, StreamObserver<ConfirmationToken> responseObserver) {

        EmailConfirmationResponseDto emailConfirmationResponseDto =
                emailConfirmationService.create(userid.getId());

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder()
                .setToken(String.valueOf(emailConfirmationResponseDto.getToken()))
                .build();

        responseObserver.onNext(confirmationToken);
        responseObserver.onCompleted();
    }

    @Override
    public void confirmUserEmail(ConfirmationToken request, StreamObserver<Empty> responseObserver) {

        emailConfirmationService.confirmEmail(request.getToken());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

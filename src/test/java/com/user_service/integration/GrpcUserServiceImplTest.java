package com.user_service.integration;

import com.user_service.AbstractIntegrationTest;
import com.user_service.config.ClockTestConfig;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.enums.UserStatus;
import com.user_service.generated.ConfirmationToken;
import com.user_service.generated.UserAuthDto;
import com.user_service.generated.UserId;
import com.user_service.generated.UserRequestDto;
import com.user_service.generated.UserResponseDto;
import com.user_service.generated.UserServiceGrpc;
import com.user_service.generated.Username;
import com.user_service.repository.EmailConfirmationRepository;
import com.user_service.repository.UserRepository;
import com.user_service.util.MutableClock;
import com.user_service.util.builder.EmailConfirmationTestBuilder;
import com.user_service.util.builder.UserTestBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest
@Import(ClockTestConfig.class)
public class GrpcUserServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfirmationRepository emailConfirmationRepository;

    @Autowired
    private MutableClock clock;

    @GrpcClient("inProcess")
    private UserServiceGrpc.UserServiceBlockingStub stub;


    @AfterEach
    void tearDown() {
        emailConfirmationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void getByUsername_Success_ReturnsUserAuthDto() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);
        userRepository.flush();

        Username username = Username.newBuilder().setUsername(user.getUsername()).build();

        UserAuthDto byUsername = stub.getByUsername(username);

        assertEquals(byUsername.getUsername(), savedUser.getUsername());
    }

    @Test
    void getByUsername_UserNotFound_ThrowsExceptionAndCheckErrorStatus() {

        Username falseUsername = Username.newBuilder().setUsername("falseUsername").build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.getByUsername(falseUsername);
        });

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }


    @Test
    void getUserByConfirmationToken_Success_ReturnsResponseDto() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        UserResponseDto userByConfirmationToken = stub.getUserByConfirmationToken(confirmationToken);

        assertEquals(userByConfirmationToken.getUsername(), savedUser.getUsername());
    }

    @Test
    void getUserByConfirmationToken_EmailConfirmationNotFound_ThrowsExceptionAndCheckErrorStatus() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(UUID.randomUUID())).build();

        StatusRuntimeException statusRuntimeException = assertThrows(StatusRuntimeException.class, () -> {

            stub.getUserByConfirmationToken(confirmationToken);
        });

        assertEquals(Status.Code.NOT_FOUND, statusRuntimeException.getStatus().getCode());
    }


    @Test
    void create_Success_ReturnsResponseDto() {

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        UserResponseDto responseDto = stub.create(requestDto);

        assertEquals(requestDto.getEmail(), responseDto.getEmail());
        assertEquals(requestDto.getUsername(), responseDto.getUsername());
    }

    @Test
    void create_UsernameAlreadyExist_ThrowsExceptionAndCheckErrorStatus() {

        User user = new UserTestBuilder().withId(null).build();
        userRepository.saveAndFlush(user);

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.create(requestDto);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }

    @Test
    void create_EmailAlreadyExist_ThrowsExceptionAndCheckErrorStatus() {

        User user = new UserTestBuilder().withId(null).withUsername("somenewusername").build();
        userRepository.saveAndFlush(user);

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.create(requestDto);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }


    @Test
    void generateEmailVerificationToken_Success_ReturnsConfirmationToken() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        UserId userId = UserId.newBuilder().setId(savedUser.getId()).build();

        ConfirmationToken confirmationToken = stub.generateEmailVerificationToken(userId);

        Optional<EmailConfirmation> tokenByID = emailConfirmationRepository.findById(UUID.fromString(confirmationToken.getToken()));

        assertFalse(tokenByID.isEmpty());
        assertEquals(tokenByID.get().getToken(), UUID.fromString(confirmationToken.getToken()));
    }


    @Test
    void generateEmailVerificationToken_UserNotFound_ThrowsExceptionAndCheckErrorStatus() {

        UserId userId = UserId.newBuilder().setId(2L).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {

            stub.generateEmailVerificationToken(userId);
        });

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }


    @Test
    void verifyUserEmail_Success_UpdateUserStatusAndTokenActivity() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();
        stub.verifyUserEmail(confirmationToken);

        User checkUser = userRepository.findById(savedUser.getId()).get();

        EmailConfirmation checkToken = emailConfirmationRepository.findById(savedConfirmation.getToken()).get();

        assertEquals(checkUser.getStatus(), UserStatus.ACTIVE);
        assertTrue(checkToken.getIsUsed());
    }

    @Test
    void verifyUserEmail_EmailConfirmationTokenExpiration_ThrowsExceptionAndCheckErrorStatus() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        clock.setInstant(Instant.parse("2025-01-02T00:00:00Z"));

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.verifyUserEmail(confirmationToken);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.UNAUTHENTICATED);
    }


    @Test
    void verifyUserEmail_EmailAlreadyActivated_ThrowsExceptionAndCheckErrorStatus() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUsed(true).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.verifyUserEmail(confirmationToken);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }
}

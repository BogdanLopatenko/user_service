package com.user_service.integration;

import com.user_service.AbstractIntegrationTest;
import com.user_service.constant.TestConstant;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.enums.UserStatus;
import com.user_service.generated.*;
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

import java.util.UUID;

import static org.junit.Assert.*;

public class GrpcUserServiceImplIT extends AbstractIntegrationTest {

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
        clock.setInstant(TestConstant.DEFAULT_INSTANT);
    }

    @Test
    void shouldReturnUserAuthDtoWhenUsernameExists() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);
        userRepository.flush();

        Username username = Username.newBuilder().setUsername(user.getUsername()).build();

        UserAuthDto byUsername = stub.getByUsername(username);

        assertEquals(byUsername.getUsername(), savedUser.getUsername());
    }

    @Test
    void shouldThrowNotFoundWhenUsernameDoesNotExist() {

        Username falseUsername = Username.newBuilder().setUsername("falseUsername").build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.getByUsername(falseUsername);
        });

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }


    @Test
    void shouldReturnUserResponseDtoWhenConfirmationTokenExists() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        UserResponseDto userByConfirmationToken = stub.getUserByConfirmationToken(confirmationToken);

        assertEquals(userByConfirmationToken.getUsername(), savedUser.getUsername());
    }

    @Test
    void shouldThrowNotFoundWhenConfirmationTokenDoesNotExist() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(UUID.randomUUID())).build();

        StatusRuntimeException statusRuntimeException = assertThrows(StatusRuntimeException.class, () -> {

            stub.getUserByConfirmationToken(confirmationToken);
        });

        assertEquals(Status.Code.NOT_FOUND, statusRuntimeException.getStatus().getCode());
    }


    @Test
    void shouldCreateUserAndReturnResponseDto() {

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        UserResponseDto createdUser = stub.create(requestDto);

        User userFromDb = userRepository.findAll().get(0);

        assertEquals(requestDto.getEmail(), createdUser.getEmail());
        assertEquals(requestDto.getUsername(), createdUser.getUsername());
        assertEquals(userFromDb.getUsername(), createdUser.getUsername());
        assertEquals(userFromDb.getEmail(), createdUser.getEmail());
    }

    @Test
    void shouldThrowAlreadyExistsWhenUsernameAlreadyExists() {

        User user = new UserTestBuilder().withId(null).build();
        userRepository.saveAndFlush(user);

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.create(requestDto);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }

    @Test
    void shouldThrowAlreadyExistsWhenEmailAlreadyExists() {

        User user = new UserTestBuilder().withId(null).withUsername("somenewusername").build();
        userRepository.saveAndFlush(user);

        UserRequestDto requestDto = new UserTestBuilder().buildProtoRequestDto();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.create(requestDto);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }

    @Test
    void shouldGenerateEmailConfirmationTokenForExistingUser() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        UserId userId = UserId.newBuilder().setId(savedUser.getId()).build();

        ConfirmationToken confirmationToken = stub.generateEmailConfirmationToken(userId);

        EmailConfirmation emailConfirmation = emailConfirmationRepository.findByToken(UUID.fromString(confirmationToken.getToken())).orElseThrow();

        assertNotNull(emailConfirmation);
        assertEquals(emailConfirmation.getToken(), UUID.fromString(confirmationToken.getToken()));
    }

    @Test
    void shouldThrowNotFoundWhenGeneratingEmailConfirmationTokenForNonExistingUser() {

        UserId userId = UserId.newBuilder().setId(9999999L).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {

            stub.generateEmailConfirmationToken(userId);
        });

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }


    @Test
    void shouldActivateUserAndMarkTokenAsUsedWhenConfirmationTokenIsValid() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();
        stub.confirmUserEmail(confirmationToken);

        User checkUser = userRepository.findById(savedUser.getId()).orElseThrow();

        EmailConfirmation checkToken = emailConfirmationRepository.findByToken(savedConfirmation.getToken()).orElseThrow();

        assertEquals(checkUser.getStatus(), UserStatus.ACTIVE);
        assertTrue(checkToken.getIsUsed());
    }

    @Test
    void shouldThrowUnauthenticatedWhenConfirmationTokenIsExpired() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder()
                .withToken(null)
                .withUser(savedUser)
                .withExpiresAtInstant(TestConstant.DEFAULT_INSTANT)
                .build();

        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        clock.setInstant(TestConstant.INSTANCE_AFTER);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.confirmUserEmail(confirmationToken);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.UNAUTHENTICATED);
    }


    @Test
    void shouldThrowAlreadyExistsWhenEmailIsAlreadyVerified() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.saveAndFlush(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUsed(true).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.saveAndFlush(emailConfirmation);

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(savedConfirmation.getToken())).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.confirmUserEmail(confirmationToken);
        });

        assertEquals(exception.getStatus().getCode(), Status.Code.ALREADY_EXISTS);
    }

    @Test
    void shouldThrowEmailConfirmationNotFoundWhenConfirmationNotExist() {

        ConfirmationToken confirmationToken = ConfirmationToken.newBuilder().setToken(String.valueOf(UUID.randomUUID())).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.confirmUserEmail(confirmationToken);
        });

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }
}

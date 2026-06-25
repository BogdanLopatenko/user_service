package com.user_service.integration;

import com.user_service.AbstractIntegrationTest;
import com.user_service.config.ClockTestConfig;
import com.user_service.constant.ConstantTest;
import com.user_service.dto.confirmation.EmailConfirmationResponseDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.entity.EmailConfirmation;
import com.user_service.entity.User;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailAlreadyActivatedException;
import com.user_service.exception.EmailConfirmationNotFoundException;
import com.user_service.exception.EmailConfirmationTokenExpirationException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.repository.EmailConfirmationRepository;
import com.user_service.repository.UserRepository;
import com.user_service.service.impl.EmailConfirmationServiceImpl;
import com.user_service.util.MutableClock;
import com.user_service.util.builder.EmailConfirmationTestBuilder;
import com.user_service.util.builder.UserTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@Import(ClockTestConfig.class)
public class EmailConfirmationServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private EmailConfirmationRepository emailConfirmationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfirmationServiceImpl emailConfirmationService;

    @Autowired
    private MutableClock clock;

    @AfterEach
    void setDefaultClockDate() {

        clock.setInstant(ConstantTest.DEFAULT_INSTANT);
    }

    @Test
    void shouldUseTestClock() {

        assertEquals(
                ConstantTest.DEFAULT_INSTANT,
                clock.instant()
        );
    }

    @Test
    void shouldCreateEmailConfirmationWhenUserExists() {

        User user = new UserTestBuilder().withId(null).build();

        User savedUser = userRepository.save(user);

        EmailConfirmationResponseDto result = emailConfirmationService.create(savedUser.getId());

        assertNotNull(result);
        assertEquals(result.getUser().getId(), savedUser.getId());
        assertFalse(result.getIsUsed());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        assertThrows(UserNotFoundException.class, () -> {
            emailConfirmationService.create(12L);
        });
    }

    @Test
    void shouldUpdateConfirmationAndActivateUserWhenTokenIsValid() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmationToken = emailConfirmationRepository.save(emailConfirmation);

        emailConfirmationService.confirmEmail(String.valueOf(savedConfirmationToken.getToken()));

        EmailConfirmation resultConfirmation = emailConfirmationRepository.findById(savedConfirmationToken.getToken()).get();
        User resultUser = userRepository.findById(savedUser.getId()).get();

        assertTrue(resultConfirmation.getIsUsed());
        assertEquals(resultUser.getStatus(), UserStatus.ACTIVE);
    }

    @Test
    void shouldThrowEmailConfirmationTokenExpirationExceptionWhenTokenIsExpired() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder()
                .withToken(null)
                .withExpiresAtInstant(ConstantTest.DEFAULT_INSTANT)
                .withUser(savedUser)
                .build();

        EmailConfirmation savedConfirmationToken = emailConfirmationRepository.save(emailConfirmation);

        clock.setInstant(ConstantTest.INSTANCE_AFTER);

        assertThrows(EmailConfirmationTokenExpirationException.class, () -> {
            emailConfirmationService.confirmEmail(String.valueOf(savedConfirmationToken.getToken()));
        });

        EmailConfirmation resultConfirmation = emailConfirmationRepository.findById(savedConfirmationToken.getToken()).get();
        User resultUser = userRepository.findById(savedUser.getId()).get();

        assertFalse(resultConfirmation.getIsUsed());
        assertEquals(resultUser.getStatus(), UserStatus.NEED_EMAIL_CONFIRMATION);
    }

    @Test
    void shouldThrowEmailAlreadyActivatedExceptionWhenEmailAlreadyActivated() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).withUsed(true).build();
        EmailConfirmation savedConfirmationToken = emailConfirmationRepository.save(emailConfirmation);

        assertThrows(EmailAlreadyActivatedException.class, () -> {
            emailConfirmationService.confirmEmail(String.valueOf(savedConfirmationToken.getToken()));
        });
    }

    @Test
    void shouldReturnUserResponseDtoWhenConfirmationTokenExists() {

        User user = new UserTestBuilder().withId(null).build();
        User savedUser = userRepository.save(user);

        EmailConfirmation emailConfirmation = new EmailConfirmationTestBuilder().withToken(null).withUser(savedUser).build();
        EmailConfirmation savedConfirmation = emailConfirmationRepository.save(emailConfirmation);

        UserResponseDto userByConfirmationToken = emailConfirmationService.getUserByConfirmationToken(String.valueOf(savedConfirmation.getToken()));

        assertEquals(userByConfirmationToken.getId(), savedUser.getId());
        assertEquals(userByConfirmationToken.getEmail(), savedUser.getEmail());
    }

    @Test
    void shouldThrowEmailConfirmationNotFoundExceptionWhenConfirmationTokenDoesNotExist() {

        EmailConfirmation build = new EmailConfirmationTestBuilder().build();

        assertThrows(EmailConfirmationNotFoundException.class, () -> {
            emailConfirmationService.getUserByConfirmationToken(String.valueOf(build.getToken()));
        });
    }
}

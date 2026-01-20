package com.user_service.service.impl;

import com.user_service.constant.ExceptionConstant;
import com.user_service.dto.user.UserAuthDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.dto.user.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.filter.UserSpecification;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.UserRepository;
import com.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto getById(Long id) {

        User userById = getUserById(id);

        log.info("Found user by ID {}", id);

        return mapper.toResponseDto(userById);
    }

    @Override
    public UserAuthDto getByUsername(String username) {

        User userByUsername = repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(ExceptionConstant.USER_NOT_FOUND_BY_USERNAME + username));

        log.info("Found user by username {}", username);

        return mapper.toAuthDto(userByUsername);
    }

    @Override
    public List<UserResponseDto> search(UserFilterDto filterDto) {

        Specification<User> userSpecification = UserSpecification.withFilters(filterDto);

        List<User> allUsers = repository.findAll(userSpecification);

        log.info("Found {} users available for this search request", allUsers.size());

        return mapper.toResponseDtoList(allUsers);
    }

    @Override
    public UserResponseDto createWithRole(UserRequestDto dto, UserRole role) {

        checkUniqueUsername(dto.getUsername());
        checkUniqueEmail(dto.getEmail());

        User user = mapper.toEntityFromRequestDto(dto);
        user.setRole(role);
        user.setStatus(UserStatus.NEED_EMAIL_CONFIRMATION);

        log.info("Start saving user");

        User savedUser = repository.save(user);

        log.info("User was successfully saved");

        return mapper.toResponseDto(savedUser);
    }

    @Override
    public void update(UserUpdateDto dto) {

        User userById = getUserById(dto.getId());

        log.info("Found user by id {}", userById.getId());

        mapper.updateFromDb(dto, userById);

        log.info("Start saving user");

        repository.save(userById);

        log.info("User was successfully saved");
    }

    @Override
    public void delete(Long id) {

        repository.deleteById(id);
    }

    private User getUserById(Long id) {

        log.info("Get UserById method, id: {}", id);

        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(ExceptionConstant.USER_NOT_FOUND_BY_ID + id));
    }

    private void checkUniqueEmail(String email) {

        log.info("Checking unique email: {}", email);

        repository.findByEmail(email).ifPresent(e -> {
            throw new EmailIsAlreadyExistException(ExceptionConstant.EMAIL_IS_ALREADY_EXIST + email);
        });

        log.info("Email {} is unique", email);
    }

    private void checkUniqueUsername(String username) {

        log.info("Checking unique email: {}", username);

        repository.findByUsername(username).ifPresent(e -> {
            throw new UsernameIsAlreadyExistException(ExceptionConstant.USERNAME_IS_ALREADY_EXIST + username);
        });

        log.info("Username {} is unique", username);
    }
}

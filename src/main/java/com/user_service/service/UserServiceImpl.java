package com.user_service.service;

import com.user_service.constant.ExceptionConstant;
import com.user_service.dto.UserRequestDto;
import com.user_service.dto.UserResponseDto;
import com.user_service.dto.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.filter.UserSpecification;
import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.exception.EmailIsAlreadyExistException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UsernameIsAlreadyExistException;
import com.user_service.mapper.UserMapper;
import com.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto getById(Long id) {

        User userById = getUserById(id);

        return mapper.toResponseDto(userById);
    }

    @Override
    public List<UserResponseDto> search(UserFilterDto filterDto) {

        Specification<User> userSpecification = UserSpecification.withFilters(filterDto);

        List<User> allUsers = repository.findAll(userSpecification);

        return mapper.toResponseDtoList(allUsers);
    }

    @Override
    public UserResponseDto createWithRole(UserRequestDto dto, UserRole role) {

        checkUniqueUsername(dto.getUsername());

        checkUniqueEmail(dto.getEmail());

        User user = mapper.toEntityFromRequestDto(dto);
        user.setRole(role);
        user.setIsActive(true);

        User savedUser = repository.save(user);

        return mapper.toResponseDto(savedUser);
    }

    @Override
    public void update(UserUpdateDto dto) {

        User userById = getUserById(dto.getId());

        mapper.updateUserFromDb(dto, userById);

        repository.save(userById);
    }

    @Override
    public void delete(Long id) {

        repository.deleteById(id);
    }

    private User getUserById(Long id){

        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(ExceptionConstant.USER_NOT_FOUND_BY_ID + id));
    }

    private void checkUniqueEmail(String email){

        repository.findByEmail(email).ifPresent(e -> {
            throw new EmailIsAlreadyExistException(ExceptionConstant.EMAIL_IS_ALREADY_EXIST + email);
        });
    }

    private void checkUniqueUsername(String username){

        repository.findByUsername(username).ifPresent(e -> {
            throw new UsernameIsAlreadyExistException(ExceptionConstant.USERNAME_IS_ALREADY_EXIST + username);
        });
    }
}

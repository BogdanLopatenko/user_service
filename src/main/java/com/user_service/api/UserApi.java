package com.user_service.api;

import com.user_service.dto.UserRequestDto;
import com.user_service.dto.UserResponseDto;
import com.user_service.dto.UserUpdateDto;
import com.user_service.dto.filter.UserFilterDto;
import com.user_service.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Users", description = "Operations for managing users")
public interface UserApi {

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    UserResponseDto getById(Long id);

    @Operation(summary = "Search users", description = "Search users based on filter criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid search filter."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    List<UserResponseDto> search(UserFilterDto filterDto);

    @Operation(summary = "Create a new user", description = "Create a new user optionally with a specific role. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "403", description = "Access denied."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    UserResponseDto createWithRole(UserRequestDto dto, UserRole userRole);

    @Operation(summary = "Update user", description = "Update an existing user's details. Access is controlled by permission validator.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "403", description = "Access denied."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    void update(UserUpdateDto dto);

    @Operation(summary = "Delete user by ID", description = "Delete a user by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    void delete(Long id);
}

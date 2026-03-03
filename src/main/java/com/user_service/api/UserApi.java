package com.user_service.api;

import com.user_service.dto.filter.UserFilterDto;
import com.user_service.dto.user.UserRequestDto;
import com.user_service.dto.user.UserResponseDto;
import com.user_service.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Users", description = "Operations for managing users")
public interface UserApi {

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

    @Operation(summary = "Generate email verification token", description = "Generates a new user email verification token..")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Code created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "403", description = "Access denied."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    String generateEmailConfirmationToken(@PathVariable Long userId);
}

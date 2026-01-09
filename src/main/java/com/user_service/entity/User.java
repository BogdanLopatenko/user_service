package com.user_service.entity;

import com.user_service.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can't be null or blank.")
    @Size(message = "Username must be in range > 4 & < 75", min = 4, max = 75)
    @Column(name = "username", nullable = false)
    private String username;

    @NotBlank(message = "Password can't be null or blank.")
    @Size(message = "Password must be in range > 4 & < 64", min = 4, max = 64)
    @Column(name = "password", nullable = false)
    private String password;

    @Size(message = "Firstname must be in range > 2 & < 50", min = 2, max = 50)
    @Column(name = "firstname")
    private String firstname;

    @Size(message = "Lastname must be in range > 2 & < 50", min = 2, max = 50)
    @Column(name = "lastname")
    private String lastname;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email can't be null or blank.")
    @Size(message = "Email must be in range > 5 & < 100", min = 5, max = 100)
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull(message = "Role can't be null.")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @NotNull(message = "Is active can't be null.")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}

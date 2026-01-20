package com.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_email_confirmation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User id can't be null.")
    private User user;

    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Expiration date can't be null.")
    @FutureOrPresent(message = "Expiration date can't be in past.")
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    @NotNull(message = "Is used can't be null.")
    private Boolean isUsed;
}
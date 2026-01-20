package com.user_service.repository;

import com.user_service.entity.UserEmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserEmailConfirmationRepository extends JpaRepository<UserEmailConfirmation, UUID> {

}
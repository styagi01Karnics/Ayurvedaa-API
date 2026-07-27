package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    Optional<AuthUser> findByTenantIdAndEmailIgnoreCaseAndDeletedFalse(UUID tenantId, String email);

    Optional<AuthUser> findByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<AuthUser> findByUsernameIgnoreCaseAndDeletedFalse(String username);

    boolean existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(UUID tenantId, String email);

    boolean existsByUsernameIgnoreCaseAndDeletedFalse(String username);

    List<AuthUser> findByTenantIdAndDeletedFalse(UUID tenantId);

    Optional<AuthUser> findByIdAndDeletedFalse(UUID id);

}

package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.enums.UserRole;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    Optional<AuthUser> findByTenantIdAndEmailIgnoreCaseAndDeletedFalse(UUID tenantId, String email);

    Optional<AuthUser> findByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(UUID tenantId, String username);

    Optional<AuthUser> findByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<AuthUser> findByUsernameIgnoreCaseAndDeletedFalse(String username);

    Optional<AuthUser> findByRoleAndUsernameIgnoreCaseAndDeletedFalse(UserRole role, String username);

    Optional<AuthUser> findByRoleAndEmailIgnoreCaseAndDeletedFalse(UserRole role, String email);

    boolean existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(UUID tenantId, String email);

    boolean existsByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(UUID tenantId, String username);

    boolean existsByUsernameIgnoreCaseAndDeletedFalse(String username);

    boolean existsByRoleAndDeletedFalse(UserRole role);

    List<AuthUser> findByTenantIdAndDeletedFalse(UUID tenantId);

    Page<AuthUser> findByTenantIdAndDeletedFalse(UUID tenantId, Pageable pageable);

    List<AuthUser> findByTenantIdAndRoleAndDeletedFalseOrderByCreatedAtDesc(UUID tenantId, UserRole role);

    Optional<AuthUser> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    Optional<AuthUser> findByIdAndDeletedFalse(UUID id);

    long countByTenantRoleIdAndDeletedFalse(UUID tenantRoleId);

}

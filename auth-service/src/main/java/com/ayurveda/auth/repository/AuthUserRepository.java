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

    Optional<AuthUser> findByTenantIdAndUserCodeIgnoreCaseAndDeletedFalse(UUID tenantId, String userCode);

    Optional<AuthUser> findByEmailIgnoreCaseAndDeletedFalse(String email);

    List<AuthUser> findAllByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<AuthUser> findByRoleAndEmailIgnoreCaseAndDeletedFalse(UserRole role, String email);

    boolean existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(UUID tenantId, String email);

    /** Includes soft-deleted rows, matching the database unique constraint on (tenant_id, user_code). */
    boolean existsByTenantIdAndUserCodeIgnoreCase(UUID tenantId, String userCode);

    boolean existsByTenantIdAndUserCodeIgnoreCaseAndIdNot(UUID tenantId, String userCode, UUID id);

    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    boolean existsByRoleAndDeletedFalse(UserRole role);

    List<AuthUser> findByTenantIdAndDeletedFalse(UUID tenantId);

    Page<AuthUser> findByTenantIdAndDeletedFalse(UUID tenantId, Pageable pageable);

    List<AuthUser> findByTenantIdAndRoleAndDeletedFalseOrderByCreatedAtDesc(UUID tenantId, UserRole role);

    Optional<AuthUser> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    Optional<AuthUser> findByIdAndDeletedFalse(UUID id);

    long countByTenantRoleIdAndDeletedFalse(UUID tenantRoleId);

}

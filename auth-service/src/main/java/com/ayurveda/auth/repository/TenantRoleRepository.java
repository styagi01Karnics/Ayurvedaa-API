package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.TenantRole;

public interface TenantRoleRepository extends JpaRepository<TenantRole, UUID> {

    List<TenantRole> findAllByTenantIdAndDeletedFalseOrderByRoleNameAsc(UUID tenantId);

    Optional<TenantRole> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    Optional<TenantRole> findByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(UUID tenantId, String roleCode);

    boolean existsByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(UUID tenantId, String roleCode);

}

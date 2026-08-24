package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.enums.TenantStatus;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

    boolean existsByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

    boolean existsBySchemaNameIgnoreCaseAndDeletedFalse(String schemaName);

    Optional<Tenant> findByIdAndDeletedFalse(UUID id);

    Optional<Tenant> findFirstByPlatformTrueAndDeletedFalse();

    List<Tenant> findAllByPlatformFalseAndDeletedFalseOrderByCreatedAtDesc();

    List<Tenant> findAllByPlatformFalseAndStatusAndDeletedFalseOrderByCreatedAtDesc(TenantStatus status);

}

package com.ayurveda.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

    boolean existsByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

}

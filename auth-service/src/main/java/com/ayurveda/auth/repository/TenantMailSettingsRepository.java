package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.TenantMailSettings;

public interface TenantMailSettingsRepository extends JpaRepository<TenantMailSettings, UUID> {

    Optional<TenantMailSettings> findByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

    List<TenantMailSettings> findAllByDeletedFalse();

}

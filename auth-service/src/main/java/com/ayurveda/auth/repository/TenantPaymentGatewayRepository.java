package com.ayurveda.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.TenantPaymentGateway;

public interface TenantPaymentGatewayRepository extends JpaRepository<TenantPaymentGateway, UUID> {

    Optional<TenantPaymentGateway> findByTenantCodeIgnoreCaseAndDeletedFalse(String tenantCode);

}

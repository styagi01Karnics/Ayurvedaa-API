package com.ayurveda.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.TenantRolePage;

public interface TenantRolePageRepository extends JpaRepository<TenantRolePage, UUID> {

    List<TenantRolePage> findAllByTenantRoleId(UUID tenantRoleId);

    List<TenantRolePage> findAllByTenantRoleIdAndDeletedFalse(UUID tenantRoleId);

    List<TenantRolePage> findAllByTenantRoleIdInAndDeletedFalse(List<UUID> tenantRoleIds);

}

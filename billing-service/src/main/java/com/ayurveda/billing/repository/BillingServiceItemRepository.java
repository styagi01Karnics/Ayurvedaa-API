package com.ayurveda.billing.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.billing.entity.BillingServiceItem;

public interface BillingServiceItemRepository extends JpaRepository<BillingServiceItem, UUID> {

    List<BillingServiceItem> findAllByBillingIdAndDeletedFalse(UUID billingId);

    List<BillingServiceItem> findAllByBillingIdInAndDeletedFalse(Collection<UUID> billingIds);

}

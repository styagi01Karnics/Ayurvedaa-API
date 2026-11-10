package com.ayurveda.notification.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.notification.entity.EmailDeliveryLog;

public interface EmailDeliveryLogRepository extends JpaRepository<EmailDeliveryLog, UUID> {

    Page<EmailDeliveryLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

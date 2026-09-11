package com.ayurveda.notification.entity;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Soft audit of outbound SMTP attempts. Never blocks callers on failure.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_delivery_logs")
public class EmailDeliveryLog extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String recipientEmail;

    @Column(length = 50)
    private String tenantCode;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 20)
    private String deliveryStatus;

    @Column(nullable = false)
    private int attempts;

    @Column(length = 100)
    private String provider;

    @Column(length = 500)
    private String errorMessage;
}

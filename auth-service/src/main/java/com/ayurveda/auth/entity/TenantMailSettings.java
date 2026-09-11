package com.ayurveda.auth.entity;

import com.ayurveda.auth.enums.HospitalMailProvider;
import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant_mail_settings", schema = "public")
public class TenantMailSettings extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String tenantCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HospitalMailProvider provider;

    @Column(nullable = false, length = 150)
    private String fromEmail;

    /** SMTP / App Password — AES-GCM encrypted at rest (ENC:v1:salt:iv:cipher). */
    @Column(nullable = false, length = 1024)
    private String smtpPassword;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

}

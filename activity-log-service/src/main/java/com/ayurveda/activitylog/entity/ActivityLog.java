package com.ayurveda.activitylog.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.activitylog.enums.ActivityAction;
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
@Table(name = "activity_logs")
public class ActivityLog extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String page;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityAction action;

    @Column(nullable = false, length = 150)
    private String target;

    @Column(name = "before_value", columnDefinition = "TEXT")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "TEXT")
    private String afterValue;

    @Column(nullable = false)
    private LocalDateTime activityTimestamp;

    private UUID performedByUserId;

    @Column(length = 150)
    private String performedByUserName;

    @Column(length = 50)
    private String performedByRole;

}

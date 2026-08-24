package com.ayurveda.auth.entity;

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
 * Global UI page/module catalog. Hospital admins assign these to custom roles.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ui_pages")
public class UiPage extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String pageCode;

    @Column(nullable = false, length = 100)
    private String pageName;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String module;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

}

package com.ayurveda.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.TenantRolePage;
import com.ayurveda.auth.entity.UiPage;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.repository.TenantRolePageRepository;
import com.ayurveda.auth.repository.UiPageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PageAccessService {

    private final UiPageRepository uiPageRepository;
    private final TenantRolePageRepository tenantRolePageRepository;

    @Transactional(readOnly = true)
    public List<String> resolvePageCodes(AuthUser user) {
        if (user.getRole() == UserRole.SUPER_ADMIN || user.getRole() == UserRole.ADMIN) {
            return uiPageRepository.findAllByDeletedFalseOrderBySortOrderAsc().stream()
                    .map(UiPage::getPageCode)
                    .toList();
        }

        if (user.getTenantRole() == null || !Boolean.TRUE.equals(user.getTenantRole().getActive())) {
            return List.of();
        }

        return tenantRolePageRepository
                .findAllByTenantRoleIdAndDeletedFalse(user.getTenantRole().getId())
                .stream()
                .map(TenantRolePage::getUiPage)
                .filter(page -> page != null && !Boolean.TRUE.equals(page.getDeleted()))
                .map(UiPage::getPageCode)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

}

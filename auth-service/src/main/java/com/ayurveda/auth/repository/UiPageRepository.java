package com.ayurveda.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.auth.entity.UiPage;

public interface UiPageRepository extends JpaRepository<UiPage, UUID> {

    List<UiPage> findAllByDeletedFalseOrderBySortOrderAsc();

    Optional<UiPage> findByPageCodeIgnoreCaseAndDeletedFalse(String pageCode);

    boolean existsByPageCodeIgnoreCaseAndDeletedFalse(String pageCode);

    List<UiPage> findAllByPageCodeInAndDeletedFalse(List<String> pageCodes);

}

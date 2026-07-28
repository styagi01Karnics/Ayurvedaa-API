package com.ayurveda.activitylog.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.activitylog.entity.ActivityLog;
import com.ayurveda.activitylog.enums.ActivityAction;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    Optional<ActivityLog> findByIdAndDeletedFalse(UUID id);

    List<ActivityLog> findAllByDeletedFalseOrderByActivityTimestampDesc();

    @Query("""
            SELECT a FROM ActivityLog a
            WHERE a.deleted = false
              AND (:page IS NULL OR LOWER(a.page) = LOWER(:page))
              AND (:action IS NULL OR a.action = :action)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(a.page) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(a.target) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(a.performedByUserName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(a.beforeValue, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(a.afterValue, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY a.activityTimestamp DESC
            """)
    List<ActivityLog> search(
            @Param("page") String page,
            @Param("action") ActivityAction action,
            @Param("search") String search);

}

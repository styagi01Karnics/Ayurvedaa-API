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

    /**
     * Avoid {@code COALESCE(textCol, '')} — Hibernate/Postgres can bind the empty literal as
     * {@code bytea}, causing {@code lower(bytea)} errors on list/search.
     */
    @Query("""
            SELECT a FROM ActivityLog a
            WHERE a.deleted = false
              AND (:page IS NULL OR LOWER(a.page) = LOWER(:page))
              AND (:action IS NULL OR a.action = :action)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(a.page) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(a.target) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR (a.performedByUserName IS NOT NULL
                        AND LOWER(a.performedByUserName) LIKE LOWER(CONCAT('%', :search, '%')))
                    OR (a.beforeValue IS NOT NULL
                        AND LOWER(a.beforeValue) LIKE LOWER(CONCAT('%', :search, '%')))
                    OR (a.afterValue IS NOT NULL
                        AND LOWER(a.afterValue) LIKE LOWER(CONCAT('%', :search, '%')))
                  )
            ORDER BY a.activityTimestamp DESC
            """)
    List<ActivityLog> search(
            @Param("page") String page,
            @Param("action") ActivityAction action,
            @Param("search") String search);

}

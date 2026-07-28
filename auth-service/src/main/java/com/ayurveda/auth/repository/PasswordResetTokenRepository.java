package com.ayurveda.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.auth.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHashAndUsedFalseAndDeletedFalse(String tokenHash);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE PasswordResetToken t
            SET t.used = true
            WHERE t.user.id = :userId
              AND t.used = false
              AND t.deleted = false
            """)
    void invalidateActiveTokensForUser(@Param("userId") UUID userId);

}

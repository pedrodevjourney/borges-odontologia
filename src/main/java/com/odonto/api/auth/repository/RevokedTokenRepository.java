package com.odonto.api.auth.repository;

import com.odonto.api.auth.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByJti(String jti);

    @Transactional
    void deleteByExpiresAtBefore(Instant now);
}

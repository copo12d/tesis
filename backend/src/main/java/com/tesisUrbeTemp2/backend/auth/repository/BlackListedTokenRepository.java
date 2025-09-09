package com.tesisUrbeTemp2.backend.auth.repository;

import com.tesisUrbeTemp2.backend.entities.account.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
}

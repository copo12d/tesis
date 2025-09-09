package com.tesisUrbe.backend.auth.repository;

import com.tesisUrbe.backend.entities.account.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
}

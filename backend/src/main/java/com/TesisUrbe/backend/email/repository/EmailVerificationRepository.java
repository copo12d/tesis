package com.tesisUrbe.backend.email.repository;

import com.tesisUrbe.backend.email.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByUserId(Long userId);
    @Query(
            value = "SELECT * FROM EmailVerification WHERE user_id = :userId ORDER BY id DESC LIMIT 1",
            nativeQuery = true
    )
    Optional<EmailVerification> findLatestByUserId(@Param("userId") Long userId);
}

package com.tesisUrbe.backend.emailManagement.repository;

import com.tesisUrbe.backend.emailManagement.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    @Query("""
                SELECT ev FROM EmailVerification ev
                WHERE ev.user.id = :userId
                ORDER BY ev.id DESC
            """)
    List<EmailVerification> findLatestByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);

}

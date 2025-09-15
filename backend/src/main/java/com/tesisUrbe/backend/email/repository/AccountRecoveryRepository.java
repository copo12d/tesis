package com.tesisUrbe.backend.email.repository;

import com.tesisUrbe.backend.email.model.AccountRecovery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRecoveryRepository extends JpaRepository<AccountRecovery, Long> {

    @Query("SELECT * FROM AccountRecovery ar WHERE ar.user_id = :userId ORDER BY id DESC LIMIT 1")
    AccountRecovery findLatestByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE AccountRecovery ar SET ar.used = true WHERE ar.id = :id")
    void markAsUsed(@Param("id") Long id);
}

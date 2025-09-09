package com.tesisUrbe.backend.users.repository;

import com.tesisUrbe.backend.users.model.PasswordRecovery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {

    Optional<PasswordRecovery> findByUserId(Long userId);
    @Query(value = "SELECT * FROM PasswordRecovery WHERE user_id = :userId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<PasswordRecovery> findLatestByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordRecovery pr SET pr.used = true WHERE pr.id = :id")
    void markAsUsed(@Param("id") Long id);
}

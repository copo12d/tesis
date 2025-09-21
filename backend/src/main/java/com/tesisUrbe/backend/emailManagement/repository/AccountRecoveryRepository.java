package com.tesisUrbe.backend.emailManagement.repository;

import com.tesisUrbe.backend.emailManagement.model.AccountRecovery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRecoveryRepository extends JpaRepository<AccountRecovery, Long> {

    @Query("SELECT ar FROM AccountRecovery ar WHERE ar.user.id = :userId ORDER BY ar.id DESC")
    List<AccountRecovery> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE AccountRecovery ar SET ar.used = true WHERE ar.id = :id")
    void markAsUsed(@Param("id") Long id);
}

package com.tesisUrbe.backend.users.repository;


import com.tesisUrbe.backend.users.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    Optional<User> findOptionalUserByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    @Query("SELECT u.isActive FROM User u WHERE u.id = :id")
    boolean isActive(@Param("id") Long id);
    @Query("SELECT u.isBlocked FROM User u WHERE u.id = :id")
    boolean isBlocked(@Param("id") Long id);
    @Query("SELECT u.isVerified FROM User u WHERE u.id = :id")
    boolean isVerified(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("update User u set u.isActive = false where u.id = :id")
    void DeactivateUser(Long id);
}

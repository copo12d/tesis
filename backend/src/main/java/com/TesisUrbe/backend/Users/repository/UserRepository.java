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
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    @Query("SELECT u.active FROM User u WHERE u.id = :id")
    boolean active(@Param("id") Long id);
    @Query("SELECT u.blocked FROM User u WHERE u.id = :id")
    boolean blocked(@Param("id") Long id);
    @Query("SELECT u.verified FROM User u WHERE u.id = :id")
    boolean verified(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update User u set u.active = false where u.id = :id")
    void DeactivateUser(Long id);

    @Modifying
    @Transactional
    @Query("update User u set u.active = true where u.id = :id")
    void ReactivateUser(Long id);

    @Modifying
    @Transactional
    @Query("update User u set u.password = :newPassword where u.id = :id")
    void NewPassport(Long id, String newPassword);


}

package com.tesisUrbe.backend.users.repository;

import com.tesisUrbe.backend.users.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    @Modifying
    @Transactional
    @Query("update User u set u.active = false where u.id = :id")
    void DeactivateUser(Long id);

}

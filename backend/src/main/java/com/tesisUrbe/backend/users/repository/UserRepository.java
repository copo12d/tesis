package com.tesisUrbe.backend.users.repository;

import com.tesisUrbe.backend.users.dto.AuthUserProjection;
import com.tesisUrbe.backend.entities.account.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    User findPublicUserById(Long id);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    @Query("""
    SELECT u.accountLocked FROM User u
    WHERE u.id = :id
    """)

    boolean isLockedUserById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.verified = true WHERE u.id = :id")
    void verifiedUserEmail(@Param("id") Long id);

    @Query("""
    SELECT u.verified FROM User u
    WHERE u.id = :id
    """)
    boolean isVerifiedUserById(@Param("id") Long id);

    Optional<User> findByIdAndIsVerifiedTrue(Long id);

    @Query("""
    SELECT u.fullName AS fullName, 
           u.userName AS userName, 
           u.password AS password, 
           r.name AS roleName,
           u.accountLocked AS accountLocked,
           u.userLocked AS userLocked,
           u.deleted AS deleted,
           u.verified AS verified
    FROM User u
    JOIN u.role r
    WHERE u.userName = :userName
    """)
    Optional<AuthUserProjection> findAuthUserByUserName(@Param("userName") String userName);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
    void deactivateUser(@Param("id") Long id);

    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.role
    WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    Page<User> searchUsersWithRoles(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.role
    """)
    Page<User> findAllWithRoles(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.accountLocked = false WHERE u.id = :id")
    void unlockUser(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.verified = true WHERE u.id = :id")
    void verifiedUserEmail(@Param("id") Long id);

    @Query("""
    SELECT u.accountLocked FROM USER u
    WHERE u.id = :id
    """)
    boolean isLockedUserById(@Param("id") Long id);

    @Query("""
    SELECT u.verified FROM USER u
    WHERE u.id = :id
    """)
    boolean isVerifiedUserById(@Param("id") Long id);
}

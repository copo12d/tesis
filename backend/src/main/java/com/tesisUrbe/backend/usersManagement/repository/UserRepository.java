package com.tesisUrbe.backend.usersManagement.repository;

import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.usersManagement.dto.AuthUserProjection;
import com.tesisUrbe.backend.entities.account.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    @Query("""
                SELECT u FROM User u
                WHERE u.id = :id AND u.deleted = false
            """)
    User findPublicUserById(@Param("id") Long id);

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

    Optional<User> findByIdAndVerifiedFalse(Long id);

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
                WHERE u.userName = :userName AND u.deleted = false
            """)
    Optional<AuthUserProjection> findAuthUserByUserName(@Param("userName") String userName);

    @Query("""
                SELECT u FROM User u
                JOIN FETCH u.role
                WHERE u.deleted = false AND (
                    LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
            """)
    Page<User> searchUsersWithRoles(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("""
                SELECT u FROM User u
                WHERE u.deleted = false
                AND (:searchTerm IS NULL OR
                       LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                       LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                       LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                AND (:role IS NULL OR u.role.name = :role)
                AND (:verified IS NULL OR u.verified = :verified)
                AND (:accountLocked IS NULL OR u.accountLocked = :accountLocked)
                AND (:userLocked IS NULL OR u.userLocked = :userLocked)
            """)
    Page<User> searchAdvanced(
            @Param("searchTerm") String searchTerm,
            @Param("role") RoleList role,
            @Param("verified") Boolean verified,
            @Param("accountLocked") Boolean accountLocked,
            @Param("userLocked") Boolean userLocked,
            Pageable pageable
    );

    @Query("""
                SELECT u FROM User u
                JOIN FETCH u.role
                WHERE u.deleted = false
            """)
    Page<User> findAllWithRoles(Pageable pageable);

    @Query("""
                SELECT u FROM User u
                WHERE u.deleted = false
                AND (:role IS NULL OR u.role.name = :role)
                AND (:verified IS NULL OR u.verified = :verified)
                AND (:accountLocked IS NULL OR u.accountLocked = :accountLocked)
                AND (:userLocked IS NULL OR u.userLocked = :userLocked)
            """)
    List<User> searchAdvancedForReport(
            @Param("role") RoleList role,
            @Param("verified") Boolean verified,
            @Param("accountLocked") Boolean accountLocked,
            @Param("userLocked") Boolean userLocked
    );

}

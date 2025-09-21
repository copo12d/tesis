package com.tesisUrbe.backend.usersManagement.repository;

import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.entities.account.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleList name);

    boolean existsByName(RoleList name);
}

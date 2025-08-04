package com.tesisUrbe.backend.users.repository;

import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleList name);
}

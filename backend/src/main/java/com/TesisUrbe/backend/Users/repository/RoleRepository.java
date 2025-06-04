package com.TesisUrbe.backend.Users.repository;

import com.TesisUrbe.backend.Users.enums.RoleList;
import com.TesisUrbe.backend.Users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleList name);
}

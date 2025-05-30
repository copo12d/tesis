package com.TesisUrbe.backend.security.repository;

import com.TesisUrbe.backend.security.enums.RoleList;
import com.TesisUrbe.backend.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleList name);
}

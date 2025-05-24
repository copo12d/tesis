package com.luisjuarez.security.repository;

import com.luisjuarez.security.enums.RoleList;
import com.luisjuarez.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleList name);
}

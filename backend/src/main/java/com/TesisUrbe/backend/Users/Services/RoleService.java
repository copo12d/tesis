package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(RoleList role) {
        return roleRepository.findByName(role);
    }
}

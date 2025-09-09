package com.tesisUrbe.backend.users.services;

import com.tesisUrbeTemp2.backend.entities.enums.RoleList;
import com.tesisUrbeTemp2.backend.entities.account.Role;
import com.tesisUrbe.backend.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Optional<Role> findByName(RoleList name) {
        return roleRepository.findByName(name);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public long count() {
        return roleRepository.count();
    }

    public boolean existsByName(RoleList role) {
        return roleRepository.existsByName(role);
    }
}

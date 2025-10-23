package com.tesisUrbe.backend.usersManagement.services;

import com.tesisUrbe.backend.entities.account.Role;
import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.usersManagement.dto.RoleRespondDto;
import com.tesisUrbe.backend.usersManagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<RoleRespondDto> getVisibleRolesForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return List.of();
        }

        boolean isSuperuser = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERUSER"));


        if (isSuperuser) {
              
            return roleRepository.findAll().stream()
                    .map(role -> RoleRespondDto.builder()
                        .id(role.getId())
                        .name(role.getName().name())
                        .value(role.getName().getDescription())
                        .build())
                    .toList();
        } else {
            return roleRepository.findAll().stream()
                    .filter(role -> role.getName() != RoleList.ROLE_SUPERUSER)
                    .map(role -> RoleRespondDto.builder()
                        .id(role.getId())
                        .name(role.getName().name())
                        .value(role.getName().getDescription())
                        .build())
                    .toList();
        }
    }
}

package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.RoleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
<<<<<<< HEAD:backend/src/main/java/com/TesisUrbe/backend/Users/Services/RoleService.java

}
=======
}
>>>>>>> e6f8111644142c390c8d851dddf2cccba9c273d7:backend/src/main/java/com/tesisUrbe/backend/users/services/RoleService.java

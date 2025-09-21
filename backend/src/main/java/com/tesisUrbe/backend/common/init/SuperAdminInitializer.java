package com.tesisUrbe.backend.common.init;

import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.entities.account.Role;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
import com.tesisUrbe.backend.usersManagement.services.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // Valores por defecto definidos en código
    private static final String DEFAULT_USERNAME = "superuser";
    private static final String DEFAULT_EMAIL = "superadmin@tesis.com";
    private static final String DEFAULT_PASSWORD = "superuser";

    public SuperAdminInitializer(UserRepository userRepository,
                                 RoleService roleService,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // 1. Crear roles si no existen
        if (roleService.count() == 0) {
            for (RoleList roleName : RoleList.values()) {
                Role role = new Role();
                role.setName(roleName);
                roleService.save(role);
            }
            System.out.println("✅ Roles base creados");
        } else {
            System.out.println("ℹ Roles ya existen, no se crean nuevamente");
        }

        // 2. Crear superusuario solo si no hay usuarios
        if (userRepository.count() == 0) {
            Role superRole = roleService.findByName(RoleList.ROLE_SUPERUSER)
                    .orElseThrow(() -> new IllegalStateException("Rol SUPERUSER no encontrado"));

            User superAdmin = new User();
            superAdmin.setFullName("Super Admin");
            superAdmin.setUserName(DEFAULT_USERNAME);
            superAdmin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            superAdmin.setEmail(DEFAULT_EMAIL);
            superAdmin.setRole(superRole);
            superAdmin.setVerified(false);
            superAdmin.setAccountLocked(false);
            superAdmin.setUserLocked(false);
            superAdmin.setDeleted(false);
            userRepository.save(superAdmin);
            System.out.printf("✅ Superusuario inicial creado: %s / %s%n", DEFAULT_USERNAME, DEFAULT_PASSWORD);
        } else {
            System.out.println("ℹ Ya existen usuarios, no se crea superusuario por defecto");
        }
    }
}

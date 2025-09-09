package com.tesisUrbe.backend.auth.component;

import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.UserRepository;
import com.tesisUrbe.backend.users.services.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    @Value("${SUPERUSER_NAME}")
    private String superuserName;

    @Value("${SUPERUSER_PASSWORD}")
    private String superuserPassword;

    @Value("${SUPERUSER_EMAIL}")
    private String superuserEmail;

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (RoleList roleName : RoleList.values()) {
            roleService.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                return roleService.save(role);
            });
        }

        if (userRepository.count() == 0) {
            Role superRole = roleService.findByName(RoleList.ROLE_SUPERUSER)
                    .orElseThrow(() -> new RuntimeException("Rol SUPERUSER no encontrado"));
            User superAdmin = new User(
                    superuserName,
                    passwordEncoder.encode(superuserPassword),
                    superuserEmail,
                    superRole
            );
            superAdmin.setActive(true);
            superAdmin.setVerified(false);
            superAdmin.setBlocked(false);

            userRepository.save(superAdmin);
            System.out.println("Superusuario inicial creado: \nUsuario: superuser\nContraseña: superuser");
        } else {
            System.out.println("Usuarios ya existen. No se necesita creación automática.");
        }
    }
}

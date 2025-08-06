package com.tesisUrbe.backend.auth.component;

import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.services.RoleService;
import com.tesisUrbe.backend.users.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userService.contarUsuarios() == 0) {
            Role superRole = roleService.findByName(RoleList.ROLE_SUPERUSER)
                    .orElseThrow(() -> new RuntimeException("Rol SUPERUSER no encontrado"));
            User superAdmin = new User();
            superAdmin.setUserName("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadmin"));
            superAdmin.setEmail("superadmin@tesis.com");
            superAdmin.setRole(superRole);
            superAdmin.setActive(true);
            superAdmin.setVerified(false);
            superAdmin.setBlocked(false);
            System.out.println("Superusuario inicial creado: \nUsuario: superadmin\nContraseña: superadmin");
        } else {
            System.out.println("🟢 Usuarios ya existen. No se necesita creación automática.");
        }
    }
}

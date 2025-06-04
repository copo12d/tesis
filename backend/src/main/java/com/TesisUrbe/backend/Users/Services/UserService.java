package com.TesisUrbe.backend.Users.Services;

import com.TesisUrbe.backend.Users.dto.NewUserDto;
import com.TesisUrbe.backend.Users.enums.RoleList;
import com.TesisUrbe.backend.Users.exceptions.RoleNotFoundException;
import com.TesisUrbe.backend.Users.exceptions.UserAlreadyExistsException;
import com.TesisUrbe.backend.Users.model.Role;
import com.TesisUrbe.backend.Users.model.User;
import com.TesisUrbe.backend.Users.repository.RoleRepository;
import com.TesisUrbe.backend.Users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName().name());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

    public void registerUser(NewUserDto newUserDto, Authentication authentication) {
        if (existByUserName(newUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }
        if (existByEmail(newUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }
        try {
            RoleList requestedRole = RoleList.valueOf(newUserDto.getRole() != null ? newUserDto.getRole() : "ROLE_USER");
            if (requestedRole == RoleList.ROLE_ADMIN) {
                if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    throw new org.springframework.security.access.AccessDeniedException("Solo un administrador puede crear usuarios administradores");
                }
            }
            Role role = roleRepository.findByName(requestedRole)
                    .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
            User user = new User(newUserDto.getUserName(), passwordEncoder.encode(newUserDto.getPassword()), newUserDto.getEmail(), role);
            save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("El correo electrónico o nombre de usuario ya está registrado");
        }
    }

    public boolean existByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}

package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.exceptions.UserAlreadyExistsException;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
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
            if (requestedRole == RoleList.ROLE_SUPERUSER) {
                if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    throw new AccessDeniedException("Solo un administrador puede crear usuarios administradores");
                }
            }
            Role role = roleService.findByName(requestedRole)
                    .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
            User user = new User(newUserDto.getUserName(), passwordEncoder.encode(newUserDto.getPassword()), newUserDto.getEmail(), role);
            save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("El correo electrónico o nombre de usuario ya está registrado");
        }
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findOptionalUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName().name());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public List<User> findAll() {
        if(userRepository.findAll().isEmpty()) {
            throw new UsernameNotFoundException("No hay usuarios registrados");
        }
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public boolean isActive(Long id) {
        return userRepository.isActive(id);
    }

    public boolean isBlocked(Long id) {
        return userRepository.isBlocked(id);
    }

    public boolean isVerified(Long id) {
        return userRepository.isVerified(id);
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public void blockUser(Long id) {
        User user = findById(id);
        user.setBlocked(true);
        save(user);
        userRepository.save(user);
    }

    public void deactivateUserById(Long id, Authentication authentication ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        RoleList requestedRole = RoleList.valueOf(authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ? "ROLE_ADMIN" : "ROLE_USER");
        if (requestedRole == RoleList.ROLE_ADMIN) {
            if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Solo un administrador puede eliminar usuarios");
            }
        }
        userRepository.DeactivateUser(id);
    }








}

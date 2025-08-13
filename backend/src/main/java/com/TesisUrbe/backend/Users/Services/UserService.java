package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.dto.RoleUpdateDto;
import com.tesisUrbe.backend.users.dto.UpdateUserDto;
import com.tesisUrbe.backend.users.dto.UserDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void registerUser(NewUserDto newUserDto, Authentication authentication) {
        if (existByUserName(newUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (existByEmail(newUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }

        try {
            RoleList requestedRole = RoleList.valueOf(
                    newUserDto.getRole() != null ? newUserDto.getRole() : "ROLE_USER"
            );

            if (requestedRole == RoleList.ROLE_SUPERUSER) {
                validarSuperUsuario(authentication);
            }

            if (requestedRole == RoleList.ROLE_ADMIN) {
                validarAdmin(authentication);
            }

            Role role = roleService.findByName(requestedRole)
                    .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

            User user = new User(
                    newUserDto.getUserName(),
                    passwordEncoder.encode(newUserDto.getPassword()),
                    newUserDto.getEmail(),
                    role
            );

            user.setActive(true);
            userRepository.save(user);

        } catch (IllegalArgumentException e) {
            throw new RoleNotFoundException("Rol inválido");
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

    public List<UserDto> findAll(Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("No autenticado");
        }

        User requester = userRepository.findOptionalUserByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));

        RoleList requesterRole = requester.getRole().getName();

        if (requesterRole == RoleList.ROLE_USER) {
            throw new AccessDeniedException("No tienes permiso para consultar todos los usuarios");
        }

        List<User> usuarios = userRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new UsernameNotFoundException("No hay usuarios registrados");
        }

        // Filtrar según el rol del solicitante
        List<User> filtrados = usuarios.stream()
                .filter(user -> {
                    RoleList targetRole = user.getRole().getName();
                    if (requesterRole == RoleList.ROLE_ADMIN) {
                        return targetRole == RoleList.ROLE_USER || targetRole == RoleList.ROLE_ADMIN;
                    }
                    return true; // SUPERUSER ve todo
                })
                .toList();

        // Mapear a DTO
        return filtrados.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getRole().getName().name(),
                        user.isActive(),
                        user.isVerified(),
                        user.isBlocked()
                ))
                .toList();
    }


    public UserDto findById(Long id, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("No autenticado");
        }

        User requester = userRepository.findOptionalUserByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario solicitado no encontrado"));

        RoleList requesterRole = requester.getRole().getName();
        RoleList targetRole = targetUser.getRole().getName();

        if (requesterRole != RoleList.ROLE_USER &&
                requesterRole != RoleList.ROLE_ADMIN &&
                requesterRole != RoleList.ROLE_SUPERUSER) {
            throw new AccessDeniedException("Rol no reconocido");
        }

        if (requesterRole == RoleList.ROLE_USER &&
                !requester.getId().equals(targetUser.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a la información de otro usuario");
        }

        if (requesterRole == RoleList.ROLE_ADMIN &&
                targetRole == RoleList.ROLE_SUPERUSER) {
            throw new AccessDeniedException("No tienes permiso para acceder a información de Super Usuarios");
        }

        return new UserDto(
                targetUser.getId(),
                targetUser.getUserName(),
                targetUser.getEmail(),
                targetUser.getRole().getName().name(),
                targetUser.isActive(),
                targetUser.isVerified(),
                targetUser.isBlocked()
        );
    }

    public void updateUser(Long userId, UpdateUserDto updateUserDto, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (existByUserName(updateUserDto.getUserName()) &&
                !user.getUserName().equals(updateUserDto.getUserName())) {
            throw new IllegalArgumentException("Nombre de usuario ya está en uso");
        }

        if (existByEmail(updateUserDto.getEmail()) &&
                !user.getEmail().equals(updateUserDto.getEmail())) {
            throw new IllegalArgumentException("Correo electrónico ya está en uso");
        }

        if (updateUserDto.getRole() != null) {
            RoleList requestedRole = updateUserDto.getRole().getName();
            if (requestedRole == RoleList.ROLE_SUPERUSER) {
                validarSuperUsuario(authentication);
            }
            if (requestedRole == RoleList.ROLE_ADMIN) {
                validarAdmin(authentication);
            }
            Role role = roleService.findByName(requestedRole)
                    .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
            user.setRole(role);
        }
        user.setUserName(updateUserDto.getUserName());
        user.setEmail(updateUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al actualizar el usuario. Posiblemente el correo o nombre de usuario ya estén registrados.");
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al actualizar el usuario.", e);
        }

    }

    public void updateRoleById(Long userId, RoleUpdateDto roleUpdateDto, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }

        validarSuperUsuario(authentication);

        try {
            RoleList requestedRole = RoleList.valueOf(roleUpdateDto.getRoleName());
            Optional<Role> optionalRole = roleService.findByName(requestedRole);

            Role role = optionalRole.orElseThrow(() ->
                    new RoleNotFoundException("Rol no encontrado: " + roleUpdateDto.getRoleName())
            );

            User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            if (user.getRole().getName() == requestedRole) {
                throw new IllegalArgumentException("El usuario ya tiene el rol especificado");
            }

            user.setRole(role);
            save(user);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nombre de rol inválido: " + roleUpdateDto.getRoleName(), e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error al actualizar el rol del usuario. Posible conflicto de datos.", e);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al actualizar el rol.", e);
        }
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

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        user.setBlocked(true);
        save(user);
        userRepository.save(user);
    }

    public void deactivateUserById(Long id, Authentication authentication ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        validarAdmin(authentication);
        userRepository.DeactivateUser(id);
    }

    private void validarSuperUsuario(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERUSER"))) {
            throw new AccessDeniedException("Solo un Super Usuario tiene permiso para realizar esta acción");
        }
    }

    private void validarAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERUSER"))) {
            throw new AccessDeniedException("Solo un Administrador o Super Usuario tiene permiso para realizar esta acción");
        }
    }

}

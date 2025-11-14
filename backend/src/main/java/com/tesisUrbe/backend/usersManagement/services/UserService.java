package com.tesisUrbe.backend.usersManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.PasswordUtils;
import com.tesisUrbe.backend.common.util.ValidationUtils;
import com.tesisUrbe.backend.emailManagement.dto.UserRecoveryDto;
import com.tesisUrbe.backend.usersManagement.dto.*;
import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.usersManagement.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.usersManagement.exceptions.UserNotFoundException;
import com.tesisUrbe.backend.entities.account.Role;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tesisUrbe.backend.common.util.NormalizationUtils;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ApiErrorFactory errorFactory;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        AuthUserProjection authUser = userRepository.findAuthUserByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        String roleName = authUser.getRoleName();
        if (roleName == null || roleName.isBlank()) {
            throw new RoleNotFoundException("El usuario no tiene un rol asignado");
        }

        return new org.springframework.security.core.userdetails.User(
                authUser.getUserName(),
                authUser.getPassword(),
                List.of(new SimpleGrantedAuthority(roleName))
        );

    }

    public Optional<AuthUserProjection> findAuthUserProjectionByUserName(String userName) {
        return userRepository.findAuthUserByUserName(userName);
    }

    @Transactional
    public ApiResponse<Void> registerPublicUser(NewPublicUserDto newPublicUserDto) {
        newPublicUserDto.setUserName(NormalizationUtils.normalizeUsername(newPublicUserDto.getUserName()));
        newPublicUserDto.setEmail(NormalizationUtils.normalizeEmail(newPublicUserDto.getEmail()));
        ValidationUtils.validateRequiredFields(newPublicUserDto);
        PasswordUtils.validatePassword(newPublicUserDto.getPassword());

        if (userRepository.existsByUserName(newPublicUserDto.getUserName())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
            );
        }
        if (userRepository.existsByEmail(newPublicUserDto.getEmail())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
            );
        }

        Role role = roleService.findByName(RoleList.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Rol ROLE_USER no encontrado"));

        User user = User.builder()
                .fullName(newPublicUserDto.getFullName())
                .userName(newPublicUserDto.getUserName())
                .password(passwordEncoder.encode(newPublicUserDto.getPassword()))
                .email(newPublicUserDto.getEmail())
                .role(role)
                .verified(false)
                .accountLocked(false)
                .userLocked(false)
                .deleted(false)
                .build();
        userRepository.save(user);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Usuario público registrado exitosamente"
        );
    }

    @Transactional
    public ApiResponse<Void> registerAdminUser(NewAdminUserDto newAdminUserDto) {
        newAdminUserDto.setUserName(NormalizationUtils.normalizeUsername(newAdminUserDto.getUserName()));
        newAdminUserDto.setEmail(NormalizationUtils.normalizeEmail(newAdminUserDto.getEmail()));
        ValidationUtils.validateRequiredFields(newAdminUserDto);
        PasswordUtils.validatePassword(newAdminUserDto.getPassword());

        if (userRepository.existsByUserName(newAdminUserDto.getUserName())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
            );
        }

        if (userRepository.existsByEmail(newAdminUserDto.getEmail())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
            );
        }

        RoleList requestedRole;
        try {
            requestedRole = RoleList.valueOf(newAdminUserDto.getRole());
        } catch (IllegalArgumentException e) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("ROLE_INVALID", "role", "Rol inválido"))
            );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        RoleList callerRole = auth.getAuthorities().stream()
                .map(a -> RoleList.valueOf(a.getAuthority()))
                .findFirst()
                .orElse(RoleList.ROLE_USER);

        if (callerRole == RoleList.ROLE_ADMIN && requestedRole == RoleList.ROLE_SUPERUSER) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("ROLE_NOT_ALLOWED", "role", "No tienes los permisos necesarios para realizar esta acción."))
            );
        }

        Role role = roleService.findByName(requestedRole)
                .orElseThrow(() -> new RoleNotFoundException("Rol " + requestedRole + " no encontrado"));

        User user = User.builder()
                .fullName(newAdminUserDto.getFullName())
                .userName(newAdminUserDto.getUserName())
                .password(passwordEncoder.encode(newAdminUserDto.getPassword()))
                .email(newAdminUserDto.getEmail())
                .role(role)
                .verified(false)
                .accountLocked(false)
                .userLocked(false)
                .deleted(false)
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", null, "El correo electrónico o nombre de usuario ya está registrado"))
            );
        }

        return errorFactory.buildSuccess(HttpStatus.CREATED, "Usuario administrativo registrado exitosamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse<PublicUserDto> getPublicUserById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String callerUsername = auth.getName();

        User user = userRepository.findPublicUserById(id);

        if (user == null) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no público o no encontrado"))
            );
        }

        if (!user.getUserName().equals(callerUsername)) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("ACCESS_DENIED", null, "No puedes acceder a la información de este usuario"))
            );
        }

        PublicUserDto dto = new PublicUserDto(
                user.getId(),
                user.getFullName(),
                user.getUserName(),
                user.getEmail(),
                user.isVerified()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Usuario público obtenido correctamente"),
                dto,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<AdminUserDto> getAdminUserById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String callerRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        User targetUser = userRepository.findPublicUserById(id);

        if (targetUser == null) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        String targetRole = targetUser.getRole().getName().toString();

        if (callerRole.equals("ROLE_ADMIN") && targetRole.equals("ROLE_SUPERUSER")) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("ACCESS_DENIED", null, "No tienes permiso para acceder a este usuario"))
            );
        }

        AdminUserDto dto = AdminUserDto.builder()
                .id(targetUser.getId())
                .fullName(targetUser.getFullName())
                .userName(targetUser.getUserName())
                .email(targetUser.getEmail())
                .role(targetUser.getRole().getName().name())
                .roleDescription(targetUser.getRole().getName().getDescription())
                .verified(targetUser.isVerified())
                .accountLocked(targetUser.isAccountLocked())
                .userLocked(targetUser.isUserLocked())
                .build();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Usuario obtenido correctamente"),
                dto,
                null
        );
    }

    public Optional<User> findByRecoveryDto(UserRecoveryDto dto) {
        String normalizedUsername = NormalizationUtils.normalizeUsername(dto.getUserName());
        String normalizedEmail = NormalizationUtils.normalizeEmail(dto.getEmail());

        if (normalizedUsername != null) {
            return userRepository.findByUserName(normalizedUsername);
        }

        if (normalizedEmail != null) {
            return userRepository.findByEmail(normalizedEmail);
        }

        return Optional.empty();
    }

    public Optional<User> findUnverifiedUserById(Long id) {
        return userRepository.findByIdAndVerifiedFalse(id);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<AdminUserDto>> getAdminAllUsers(int page, int size, String sortBy, String sortDir, String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String callerRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = StringUtils.hasText(search)
                ? userRepository.searchUsersWithRoles(search, pageable)
                : userRepository.findAllWithRoles(pageable);

        Page<AdminUserDto> filteredPage = new PageImpl<>(
                usersPage.getContent().stream()
                        .filter(user -> callerRole.equals("ROLE_SUPERUSER") || user.getRole().getName() != RoleList.ROLE_SUPERUSER)
                        .map(user -> AdminUserDto.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .role(user.getRole().getName().name())
                                .roleDescription(user.getRole().getName().getDescription())
                                .verified(user.isVerified())
                                .accountLocked(user.isAccountLocked())
                                .userLocked(user.isUserLocked())
                                .build())
                        .toList(),
                usersPage.getPageable(),
                usersPage.getTotalElements()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Usuarios obtenidos correctamente"),
                filteredPage,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<AdminUserDto>> searchAdvanced(
            String searchTerm,
            String role,
            Boolean verified,
            Boolean accountLocked,
            Boolean userLocked,
            Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        String callerRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        RoleList roleEnum = null;
        if (StringUtils.hasText(role)) {
            try {
                roleEnum = RoleList.valueOf(role);
            } catch (IllegalArgumentException e) {
                return errorFactory.build(HttpStatus.BAD_REQUEST,
                        List.of(new ApiError("INVALID_ROLE", "role", "Rol inválido: " + role)));
            }
        }

        Page<User> usersPage = userRepository.searchAdvanced(
                searchTerm, roleEnum, verified, accountLocked, userLocked, pageable
        );

        Page<AdminUserDto> filteredPage = new PageImpl<>(
                usersPage.getContent().stream()
                        .filter(user -> callerRole.equals("ROLE_SUPERUSER") || user.getRole().getName() != RoleList.ROLE_SUPERUSER)
                        .map(user -> AdminUserDto.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .role(user.getRole().getName().name())
                                .roleDescription(user.getRole().getName().getDescription())
                                .verified(user.isVerified())
                                .accountLocked(user.isAccountLocked())
                                .userLocked(user.isUserLocked())
                                .build())
                        .toList(),
                usersPage.getPageable(),
                usersPage.getTotalElements()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Búsqueda avanzada de usuarios completada"),
                filteredPage,
                null
        );
    }

    public ApiResponse<Boolean> existByUserName(String userName) {
        boolean exists = userRepository.existsByUserName(userName);
        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Verificación de existencia de nombre de usuario completada"
        ), exists, null);
    }

    public ApiResponse<Boolean> existByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Verificación de existencia de correo electrónico completada"
        ), exists, null);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    public ApiResponse<Long> getIdByUserName(String username) {
        Optional<User> optionalUser = userRepository.findByUserName(username);
        if (optionalUser.isPresent()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.OK, "Id obtenido exitosamente"),
                    optionalUser.get().getId(),
                    null
            );
        } else {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Usuario no encontrado"),
                    null,
                    List.of(new ApiError("USER_NOT_FOUND", "username", "No se encontró el usuario con username: " + username))
            );
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public ApiResponse<Void> lockAccount(String userName) {
        return userRepository.findByUserName(userName)
                .map(user -> {
                    if (!user.isAccountLocked()) {
                        user.setAccountLocked(true);
                        userRepository.save(user);
                        return errorFactory.buildSuccess(HttpStatus.OK, "Cuenta bloqueada exitosamente"
                        );
                    } else {
                        return errorFactory.<Void>build(
                                HttpStatus.CONFLICT,
                                List.of(new ApiError("ACCOUNT_ALREADY_LOCKED", null, "La cuenta ya estaba bloqueada"))
                        );
                    }
                })
                .orElseGet(() -> errorFactory.build(
                        HttpStatus.NOT_FOUND,
                        List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
                ));
    }

    public void updatePassword(String encodedPassword, Long userId, boolean unlock) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setPassword(encodedPassword);
        if (unlock) {
            user.setUserLocked(false);
        }
        userRepository.save(user);
    }

    @Transactional
    public ApiResponse<Void> updatePublicUser(Long userId, UpdatePublicUserDto updateUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String callerUsername = auth.getName();

        try {
            User currentUser = getUserById(userId); // lanza UserNotFoundException si no existe

            if (!currentUser.getUserName().equals(callerUsername)) {
                return errorFactory.build(
                        HttpStatus.FORBIDDEN,
                        List.of(new ApiError("ACCESS_DENIED", null, "No puedes modificar el perfil de otro usuario"))
                );
            }

            if (updateUserDto.getUserName() != null && !updateUserDto.getUserName().isBlank()) {
                String normalizedUsername = NormalizationUtils.normalizeUsername(updateUserDto.getUserName());
                if (!currentUser.getUserName().equals(normalizedUsername)
                        && Boolean.TRUE.equals(existByUserName(normalizedUsername).data())) {
                    return errorFactory.build(
                            HttpStatus.CONFLICT,
                            List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
                    );
                }
                currentUser.setUserName(normalizedUsername);
            }

            if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isBlank()) {
                String normalizedEmail = NormalizationUtils.normalizeEmail(updateUserDto.getEmail());
                if (!currentUser.getEmail().equals(normalizedEmail)
                        && Boolean.TRUE.equals(existByEmail(normalizedEmail).data())) {
                    return errorFactory.build(
                            HttpStatus.CONFLICT,
                            List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
                    );
                }
                currentUser.setEmail(normalizedEmail);
                currentUser.setVerified(false);
            }

            if (updateUserDto.getFullName() != null && !updateUserDto.getFullName().isBlank()) {
                currentUser.setFullName(updateUserDto.getFullName());
            }

            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
                PasswordUtils.validatePassword(updateUserDto.getPassword());
                currentUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            }

            userRepository.save(currentUser);
            return errorFactory.buildSuccess(HttpStatus.OK, "Perfil público actualizado exitosamente");

        } catch (UserNotFoundException ex) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, ex.getMessage()))
            );
        } catch (DataIntegrityViolationException e) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", null, "El correo electrónico o nombre de usuario ya está registrado"))
            );
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al actualizar el usuario"))
            );
        }
    }

    @Transactional
    public ApiResponse<Void> updateAdminUser(Long userId, UpdateAdminUserDto updateUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        RoleList callerRole = auth.getAuthorities().stream()
                .map(a -> {
                    try {
                        return RoleList.valueOf(a.getAuthority());
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(RoleList.ROLE_USER);

        try {
            User targetUser = getUserById(userId); // lanza UserNotFoundException si no existe

            RoleList targetRole = targetUser.getRole().getName();

            if (callerRole == RoleList.ROLE_ADMIN && targetRole == RoleList.ROLE_SUPERUSER) {
                return errorFactory.build(
                        HttpStatus.FORBIDDEN,
                        List.of(new ApiError("ACCESS_DENIED", null, "No tienes permiso para modificar este usuario"))
                );
            }

            if (updateUserDto.getUserName() != null && !updateUserDto.getUserName().isBlank()) {
                String normalizedUsername = NormalizationUtils.normalizeUsername(updateUserDto.getUserName());
                if (!targetUser.getUserName().equals(normalizedUsername)
                        && Boolean.TRUE.equals(existByUserName(normalizedUsername).data())) {
                    return errorFactory.build(
                            HttpStatus.CONFLICT,
                            List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
                    );
                }
                targetUser.setUserName(normalizedUsername);
            }

            if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isBlank()) {
                String normalizedEmail = NormalizationUtils.normalizeEmail(updateUserDto.getEmail());
                if (!targetUser.getEmail().equals(normalizedEmail)
                        && Boolean.TRUE.equals(existByEmail(normalizedEmail).data())) {
                    return errorFactory.build(
                            HttpStatus.CONFLICT,
                            List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
                    );
                }
                targetUser.setEmail(normalizedEmail);
                targetUser.setVerified(false);
            }

            if (updateUserDto.getFullName() != null && !updateUserDto.getFullName().isBlank()) {
                targetUser.setFullName(updateUserDto.getFullName());
            }

            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
                PasswordUtils.validatePassword(updateUserDto.getPassword());
                targetUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            }

            if (Boolean.TRUE.equals(updateUserDto.getUnlockAccount())) {
                targetUser.setAccountLocked(false);
            }

            if (Boolean.TRUE.equals(updateUserDto.getUnlockUser())) {
                targetUser.setUserLocked(false);
            }

            if (Boolean.TRUE.equals(updateUserDto.getDeleteUser())) {
                if (callerRole != RoleList.ROLE_SUPERUSER) {
                    return errorFactory.build(
                            HttpStatus.FORBIDDEN,
                            List.of(new ApiError("ACCESS_DENIED", "deleteUser", "Solo un superusuario puede eliminar usuarios"))
                    );
                }
                targetUser.setDeleted(true);
            }

            if (updateUserDto.getRole() != null && !updateUserDto.getRole().isBlank()) {
                if (callerRole != RoleList.ROLE_SUPERUSER) {
                    return errorFactory.build(
                            HttpStatus.FORBIDDEN,
                            List.of(new ApiError("ACCESS_DENIED", "newRole", "Solo un superusuario puede cambiar el rol de un usuario"))
                    );
                }

                try {
                    RoleList requestedRole = RoleList.valueOf(updateUserDto.getRole());
                    Optional<Role> roleOpt = roleService.findByName(requestedRole);
                    if (roleOpt.isEmpty()) {
                        return errorFactory.build(
                                HttpStatus.BAD_REQUEST,
                                List.of(new ApiError("INVALID_ROLE", "newRole", "El rol solicitado no existe"))
                        );
                    }
                    Role roleEntity = roleOpt.get();
                    targetUser.setRole(roleEntity);
                } catch (IllegalArgumentException ex) {
                    return errorFactory.build(
                            HttpStatus.BAD_REQUEST,
                            List.of(new ApiError("INVALID_ROLE", "newRole", "Rol inválido"))
                    );
                }
            }

            userRepository.save(targetUser);
            return errorFactory.buildSuccess(HttpStatus.OK, "Usuario administrativo actualizado exitosamente");

        } catch (UserNotFoundException ex) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, ex.getMessage()))
            );
        } catch (DataIntegrityViolationException e) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("USER_ALREADY_EXISTS", null, "El correo electrónico o nombre de usuario ya está registrado"))
            );
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al actualizar el usuario"))
            );
        }
    }

    @Transactional
    public ApiResponse<Void> softDeleteUser(Long userId) {
        Optional<User> targetUserOpt = userRepository.findById(userId);

        if (targetUserOpt.isEmpty()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        User targetUser = targetUserOpt.get();
        targetUser.setDeleted(true);

        try {
            userRepository.save(targetUser);
            return errorFactory.buildSuccess(HttpStatus.OK, "Usuario eliminado exitosamente");
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al eliminar el usuario"))
            );
        }
    }

    public void markAsVerified(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setVerified(true);
        userRepository.save(user);
    }

}
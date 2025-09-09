package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.PasswordUtils;
import com.tesisUrbe.backend.common.util.ValidationUtils;
import com.tesisUrbe.backend.users.dto.*;
import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.exceptions.UserNotFoundException;
import com.tesisUrbe.backend.entities.account.Role;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.users.repository.UserRepository;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ApiErrorFactory errorFactory;
    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;

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
                .userName(newPublicUserDto.getUserName())
                .password(passwordEncoder.encode(newPublicUserDto.getPassword()))
                .email(newPublicUserDto.getEmail())
                .role(role)
                .active(true)
                .verified(false)
                .accountLocked(false)
                .userLocked(false)
                .deleted(false)
                .build();
        userRepository.save(user);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Usuario público registrado exitosamente"
        );
    }

    //validar que el token sea valido
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
                    List.of(new ApiError("ROLE_NOT_ALLOWED", "role", "No tienes permisos para asignar el rol SUPERUSER"))
            );
        }

        Role role = roleService.findByName(requestedRole)
                .orElseThrow(() -> new RoleNotFoundException("Rol " + requestedRole + " no encontrado"));

        User user = User.builder()
                .userName(newAdminUserDto.getUserName())
                .password(passwordEncoder.encode(newAdminUserDto.getPassword()))
                .email(newAdminUserDto.getEmail())
                .role(role)
                .active(true)
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
                user.isActive(),
                user.isVerified(),
                user.isAccountLocked(),
                user.isUserLocked(),
                user.isDeleted()
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

        String callerUsername = auth.getName();
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

        AdminUserDto dto = new AdminUserDto(
                targetUser.getId(),
                targetUser.getUserName(),
                targetUser.getFullName(),
                targetUser.getEmail(),
                targetUser.getRole().getName().name(),
                targetUser.isActive(),
                targetUser.isVerified(),
                targetUser.isAccountLocked(),
                targetUser.isUserLocked(),
                targetUser.isDeleted()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Usuario obtenido correctamente"),
                dto,
                null
        );
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
                        .map(user -> new AdminUserDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUserName(),
                                user.getEmail(),
                                user.getRole().getName().name(),
                                user.isActive(),
                                user.isVerified(),
                                user.isAccountLocked(),
                                user.isUserLocked(),
                                user.isDeleted()
                        ))
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
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
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
                .orElseGet(() -> errorFactory.<Void>build(
                        HttpStatus.NOT_FOUND,
                        List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
                ));
    }

//    @Transactional
//    public ApiResponse<Void> unlockUserAccount(Long userId) {
//        User targetUser = getUserById(userId);
//
//        if (targetUser == null) {
//            return errorFactory.<Void>build(
//                    HttpStatus.NOT_FOUND,
//                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
//            );
//        }
//
//        if (!targetUser.isBlocked()) {
//            return errorFactory.<Void>build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("ACCOUNT_NOT_BLOCKED", null, "La cuenta no está bloqueada"))
//            );
//        }
//
//        targetUser.setBlocked(false);
//        userRepository.save(targetUser);
//
//        return errorFactory.buildSuccess(HttpStatus.OK, "Cuenta desbloqueada exitosamente"
//        );
//    }

//    @Transactional
//    public ApiResponse<Void> softDeleteUser(Long userId) {
//        User targetUser = getUserById(userId);
//
//        if (targetUser == null) {
//            return errorFactory.<Void>build(
//                    HttpStatus.NOT_FOUND,
//                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
//            );
//        }
//
//        targetUser.setActive(false);
//        targetUser.setBlocked(true);
//        targetUser.setDeleted(true);
//        userRepository.save(targetUser);
//
//        return errorFactory.buildSuccess(HttpStatus.OK, "Usuario eliminado lógicamente exitosamente"
//        );
//    }

//    @Transactional
//    public ApiResponse<Void> updatePublicUser(Long userId, UpdatePublicUserDto updateUserDto) {
//        User currentUser = getUserById(userId);
//        updateUserDto.setUserName(NormalizationUtils.normalizeUsername(updateUserDto.getUserName()));
//        updateUserDto.setEmail(NormalizationUtils.normalizeEmail(updateUserDto.getEmail()));
//        ValidationUtils.validateRequiredFields(updateUserDto);
//
//        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
//            PasswordUtils.validatePassword(updateUserDto.getPassword());
//            currentUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
//        }
//
//        if (!currentUser.getUserName().equals(updateUserDto.getUserName())
//                && Boolean.TRUE.equals(existByUserName(updateUserDto.getUserName()).data())) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
//            );
//        }
//
//        if (!currentUser.getEmail().equals(updateUserDto.getEmail())
//                && Boolean.TRUE.equals(existByEmail(updateUserDto.getEmail()).data())) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
//            );
//        }
//
//        if (!currentUser.getEmail().equals(updateUserDto.getEmail())) {
//            currentUser.setVerified(false);
//        }
//
//        currentUser.setUserName(updateUserDto.getUserName());
//        currentUser.setEmail(updateUserDto.getEmail());
//
//        try {
//            userRepository.save(currentUser);
//        } catch (DataIntegrityViolationException e) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", null, "El correo electrónico o nombre de usuario ya está registrado"))
//            );
//        }
//
//        return errorFactory.buildSuccess(HttpStatus.OK, "Perfil público actualizado exitosamente"
//        );
//    }
//
//    @Transactional
//    public ApiResponse<Void> updateAdminUser(Long userId, UpdateAdminUserDto updateUserDto, RoleList callerRole) {
//        User targetUser = getUserById(userId);
//
//        if (!targetUser.getUserName().equals(updateUserDto.getUserName())
//                && Boolean.TRUE.equals(existByUserName(updateUserDto.getUserName()).data())) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", "userName", "El nombre de usuario ya existe"))
//            );
//        }
//
//        if (!targetUser.getEmail().equals(updateUserDto.getEmail())
//                && Boolean.TRUE.equals(existByEmail(updateUserDto.getEmail()).data())) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", "email", "El correo electrónico ya está registrado"))
//            );
//        }
//
//        updateUserDto.setUserName(NormalizationUtils.normalizeUsername(updateUserDto.getUserName()));
//        updateUserDto.setEmail(NormalizationUtils.normalizeEmail(updateUserDto.getEmail()));
//        ValidationUtils.validateRequiredFields(updateUserDto);
//
//        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
//            PasswordUtils.validatePassword(updateUserDto.getPassword());
//            targetUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
//        }
//
//        targetUser.setUserName(updateUserDto.getUserName());
//        targetUser.setEmail(updateUserDto.getEmail());
//
//        if (updateUserDto.getIsActive() != null) targetUser.setActive(updateUserDto.getIsActive());
//        if (updateUserDto.getIsVerified() != null) targetUser.setVerified(updateUserDto.getIsVerified());
//        if (updateUserDto.getIsBlocked() != null) targetUser.setBlocked(updateUserDto.getIsBlocked());
//
//        if (updateUserDto.getRole() != null) {
//            RoleList requestedRole = updateUserDto.getRole().getName();
//
//            if (callerRole == RoleList.ROLE_ADMIN && requestedRole == RoleList.ROLE_SUPERUSER) {
//                return errorFactory.build(
//                        HttpStatus.FORBIDDEN,
//                        List.of(new ApiError("ROLE_NOT_ALLOWED", "role", "Un administrador no puede asignar el rol SUPERUSER"))
//                );
//            }
//
//            Role role = roleService.findByName(requestedRole)
//                    .orElseThrow(() -> new RoleNotFoundException("Rol " + requestedRole + " no encontrado"));
//            targetUser.setRole(role);
//        }
//
//        try {
//            userRepository.save(targetUser);
//        } catch (DataIntegrityViolationException e) {
//            return errorFactory.build(
//                    HttpStatus.CONFLICT,
//                    List.of(new ApiError("USER_ALREADY_EXISTS", null, "El correo electrónico o nombre de usuario ya está registrado"))
//            );
//        }
//
//        return errorFactory.buildSuccess(HttpStatus.OK, "Usuario administrativo actualizado exitosamente"
//        );
//    }

    private PublicUserDto mapToDto(User user) {
        return new PublicUserDto(
                user.getId(),
                user.getFullName(),
                user.getUserName(),
                user.getEmail(),
                user.isActive(),
                user.isVerified(),
                user.isAccountLocked(),
                user.isUserLocked(),
                user.isDeleted()
        );
    }

    }
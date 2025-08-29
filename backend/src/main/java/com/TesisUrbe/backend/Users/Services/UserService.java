package com.tesisUrbe.backend.users.services;

import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.dto.UpdateAdminUserDto;
import com.tesisUrbe.backend.users.dto.UpdatePublicUserDto;
import com.tesisUrbe.backend.users.dto.UserDto;
import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.exceptions.UserAlreadyExistsException;
import com.tesisUrbe.backend.users.model.PasswordRecovery;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.PasswordRecoveryRepostory;
import com.tesisUrbe.backend.users.repository.UserRepository;
import com.tesisUrbe.backend.users.utils.UserUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.Utilities;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRecoveryRepostory passwordRecoveryRepostory;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, PasswordRecoveryRepostory passwordRecoveryRepostory) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.passwordRecoveryRepostory = passwordRecoveryRepostory;
    }

    public void registerPublicUser(NewUserDto newUserDto) {
        UserUtils.normalizeUsername(newUserDto.getUserName());
        UserUtils.normalizeEmail(newUserDto.getEmail());
        UserUtils.validateRequiredFields(newUserDto);
        UserUtils.validatePassword(newUserDto.getPassword());
        if (existByUserName(newUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }
        if (existByEmail(newUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }
        Role role = roleService.findByName(RoleList.ROLE_USER).orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        User user = new User(
                newUserDto.getUserName(),
                passwordEncoder.encode(newUserDto.getPassword()),
                newUserDto.getEmail(),
                role
        );
        user.setActive(true);
        user.setVerified(false);
        user.setBlocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void registerAdminUser(NewUserDto newUserDto, Authentication authentication) {
        UserUtils.normalizeUsername(newUserDto.getUserName());
        UserUtils.normalizeEmail(newUserDto.getEmail());
        UserUtils.validateRequiredFields(newUserDto);
        UserUtils.validatePassword(newUserDto.getPassword());
        if (existByUserName(newUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }
        if (existByEmail(newUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }
        try {
            RoleList requestedRole = (newUserDto.getRole() != null)
                    ? RoleList.valueOf(newUserDto.getRole())
                    : RoleList.ROLE_USER;
            if (requestedRole == RoleList.ROLE_SUPERUSER) {
                UserUtils.validarSuperUsuario(authentication);
            } else if (requestedRole == RoleList.ROLE_ADMIN) {
                UserUtils.validarAdmin(authentication);
            } else if (requestedRole != RoleList.ROLE_USER) {
                throw new RoleNotFoundException("Rol no permitido en este contexto");
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
            user.setVerified(false);
            user.setBlocked(false);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RoleNotFoundException("Rol inválido");
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("El correo electrónico o nombre de usuario ya está registrado");
        }
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

    public Long getUserIdByUserName(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + userName);
        }
        User user = optionalUser.get();
        return user.getId();
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public void lockUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));
        if (!user.isBlocked()) {
            user.setBlocked(true);
            userRepository.save(user);
        }
    }

    public void unlockUserAccount(Long userId, Authentication authentication) {
        UserUtils.validarAdmin(authentication);
        String currentUsername = authentication.getName();
        User requestingUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (targetUser.getRole().getName() == RoleList.ROLE_SUPERUSER &&
                requestingUser.getRole().getName() != RoleList.ROLE_SUPERUSER) {
            throw new AccessDeniedException("Solo un Super Usuario puede desbloquear a otro Super Usuario");
        }
        if (targetUser.isBlocked()) {
            targetUser.setBlocked(false);
            userRepository.save(targetUser);
        }
    }

    public void softDeleteUser(Long userId, Authentication authentication) {
        UserUtils.validarAdmin(authentication);
        String currentUsername = authentication.getName();
        User requestingUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (targetUser.getRole().getName() == RoleList.ROLE_SUPERUSER &&
                requestingUser.getRole().getName() != RoleList.ROLE_SUPERUSER) {
            throw new AccessDeniedException("Solo un Super Usuario puede eliminar a otro Super Usuario");
        }
        targetUser.setActive(false);
        targetUser.setBlocked(true);
        targetUser.setDeleted(true);
        userRepository.save(targetUser);
    }

    public void updatePublicUser(Long userId, UpdatePublicUserDto updateUserDto, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para modificar otro usuario");
        }
        UserUtils.normalizeUsername(updateUserDto.getUserName());
        UserUtils.normalizeEmail(updateUserDto.getEmail());
        UserUtils.validateRequiredFields(updateUserDto);
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            UserUtils.validatePassword(updateUserDto.getPassword());
        }
        if (!currentUser.getUserName().equals(updateUserDto.getUserName()) && existByUserName(updateUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }
        if (!currentUser.getEmail().equals(updateUserDto.getEmail()) && existByEmail(updateUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }
        currentUser.setUserName(updateUserDto.getUserName());
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }
        currentUser.setEmail(updateUserDto.getEmail());
        try {
            userRepository.save(currentUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al actualizar el usuario. Posiblemente el correo o nombre de usuario ya estén registrados.");
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al actualizar el usuario.", e);
        }
    }

    public void updateAdminUser(Long userId, UpdateAdminUserDto updateUserDto, Authentication authentication) {
        UserUtils.validarAdmin(authentication);
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (!targetUser.getUserName().equals(updateUserDto.getUserName()) && existByUserName(updateUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }
        if (!targetUser.getEmail().equals(updateUserDto.getEmail()) && existByEmail(updateUserDto.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado");
        }
        UserUtils.normalizeUsername(updateUserDto.getUserName());
        UserUtils.normalizeEmail(updateUserDto.getEmail());
        UserUtils.validateRequiredFields(updateUserDto);
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            UserUtils.validatePassword(updateUserDto.getPassword());
            targetUser.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }
        targetUser.setUserName(updateUserDto.getUserName());
        targetUser.setEmail(updateUserDto.getEmail());

        if (updateUserDto.getRole() != null) {
            RoleList requestedRole = updateUserDto.getRole().getName();
            if (requestedRole == RoleList.ROLE_SUPERUSER) {
                UserUtils.validarSuperUsuario(authentication);
            }
            Role role = roleService.findByName(requestedRole)
                    .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
            targetUser.setRole(role);
        }
        try {
            userRepository.save(targetUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al actualizar el usuario. Posiblemente el correo o nombre de usuario ya estén registrados.");
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado al actualizar el usuario.", e);
        }
    }

    public UserDto findPublicUserById(Long userId, Authentication authentication) {

        if (authentication == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }

        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para acceder a la información de otro usuario");
        }

        return new UserDto(
                currentUser.getId(),
                currentUser.getUserName(),
                currentUser.getEmail(),
                currentUser.getRole().getName().name(),
                currentUser.isActive(),
                currentUser.isVerified(),
                currentUser.isBlocked()
        );
    }

    public UserDto findAdminUserById(Long userId, Authentication authentication) {
        UserUtils.validarAdmin(authentication);

        String currentUsername = authentication.getName();
        User requestingUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        RoleList requesterRole = requestingUser.getRole().getName();
        RoleList targetRole = targetUser.getRole().getName();

        if (requesterRole == RoleList.ROLE_ADMIN &&
                targetRole != RoleList.ROLE_USER &&
                targetRole != RoleList.ROLE_ADMIN) {
            throw new AccessDeniedException("No tienes permisos para acceder a la información de este usuario");
        }

        return new UserDto(
                targetUser.getId(),
                targetUser.getUserName(),
                targetUser.getEmail(),
                targetRole.name(),
                targetUser.isActive(),
                targetUser.isVerified(),
                targetUser.isBlocked()
        );
    }

    public List<UserDto> findAllAdminUsers(Authentication authentication) {
        UserUtils.validarAdmin(authentication);
        String currentUsername = authentication.getName();
        User requestingUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));
        boolean isSuperUser = requestingUser.getRole().getName() == RoleList.ROLE_SUPERUSER;
        List<User> allUsers = userRepository.findAll();
        List<UserDto> result = new ArrayList<>();
        for (User user : allUsers) {
            RoleList targetRole = user.getRole().getName();
            if (isSuperUser) {
                result.add(new UserDto(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        targetRole.name(),
                        user.isActive(),
                        user.isVerified(),
                        user.isBlocked()
                ));
            } else {
                if (targetRole == RoleList.ROLE_USER || targetRole == RoleList.ROLE_ADMIN) {
                    result.add(new UserDto(
                            user.getId(),
                            user.getUserName(),
                            user.getEmail(),
                            targetRole.name(),
                            user.isActive(),
                            user.isVerified(),
                            user.isBlocked()
                    ));
                }
            }
        }
        return result;
    }

    public void passwordRecovery(Long userId, String token, String password){
        PasswordRecovery passwordRecovery = passwordRecoveryRepostory.findLatestByUserId(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
        if (!Objects.equals(passwordRecovery.getRecovery_token(), token)){
            throw new AccessDeniedException("No tiene el acceso a esta operacion");
        }
        if (LocalDateTime.now().isAfter(passwordRecovery.getExpiration_date())){
            throw new AccessDeniedException("Token Vencido, Solicite otro");
        }
        if (passwordRecovery.isUsed()){
            throw new AccessDeniedException("Token Usado, Solicite otro");
        }

        UserUtils.validatePassword(password);

        try {
            userRepository.NewPassport(userId, passwordEncoder.encode(password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



}

package com.tesisUrbe.backend.emailManagement.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.LinkBuilderUtils;
import com.tesisUrbe.backend.common.util.MailUtils;
import com.tesisUrbe.backend.common.util.PasswordUtils;
import com.tesisUrbe.backend.common.util.RandomTokenUtils;
import com.tesisUrbe.backend.emailManagement.dto.UserRecoveryDto;
import com.tesisUrbe.backend.emailManagement.enums.EmailList;
import com.tesisUrbe.backend.emailManagement.model.PasswordRecovery;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final UserService userService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final AccountRecoveryService accountRecoveryService;
    private final EmailVerificationService emailVerificationService;
    private final ApiErrorFactory errorFactory;
    private final JavaMailSender mailSender;

    @Value("${EMAIL_USER}")
    private String emailUser;

    @Value("${api.base-path}")
    private String domain;

    @Transactional
    public ApiResponse<Void> passwordRecoveryRequest(UserRecoveryDto dto) {
        return userService.findByRecoveryDto(dto)
                .map(user -> {
                    String token = RandomTokenUtils.randomToken(user.getUserName());
                    passwordRecoveryService.saveRecoveryRequest(user, token);
                    sendRecoveryEmail(user, "password-recovery", EmailList.PASSWORD_RECOVERY, token);
                    return errorFactory.buildSuccess(HttpStatus.OK, "Email de recuperación de contraseña enviado exitosamente");
                })
                .orElseGet(() -> new ApiResponse<>(
                        errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Usuario no encontrado"),
                        null,
                        List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
                ));
    }

    @Transactional
    public ApiResponse<Void> passwordRecovery(Long id, String token, String newPassword) {
        ApiResponse<PasswordRecovery> validation = passwordRecoveryService.validateToken(id, token);
        if (validation.meta().status() != HttpStatus.OK.value()) {
            return new ApiResponse<>(validation.meta(), null, validation.errors());
        }

        PasswordUtils.validatePassword(newPassword);
        String encoded = PasswordUtils.encode(newPassword, userService.getPasswordEncoder());
        userService.updatePassword(encoded, id);

        return errorFactory.buildSuccess(HttpStatus.OK, "Nueva contraseña para la cuenta creada exitosamente");
    }

    @Transactional
    public ApiResponse<Void> accountRecoveryRequest(UserRecoveryDto dto) {
        Optional<User> userOpt = userService.findByRecoveryDto(dto);

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Usuario no encontrado"),
                    null,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        User user = userOpt.get();

        if (!user.isAccountLocked()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.CONFLICT, "La cuenta no está bloqueada"),
                    null,
                    List.of(new ApiError("ACCOUNT_NO_LOCKED", null, "La cuenta no está bloqueada"))
            );
        }

        String token = RandomTokenUtils.randomToken(user.getUserName());
        accountRecoveryService.saveRecoveryRequest(user, token);
        sendRecoveryEmail(user, "account-recovery", EmailList.ACCOUNT_RECOVERY, token);

        return errorFactory.buildSuccess(HttpStatus.OK, "Email de recuperación de cuenta enviado exitosamente");
    }


    @Transactional
    public ApiResponse<Void> emailVerificationRequest(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String callerUsername = auth.getName();
        Optional<User> userOpt = userService.findUnverifiedUserById(id);

        if (userOpt.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Usuario ya verificado"),
                    null,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario ya verificado"))
            );
        }

        User user = userOpt.get();

        if (!user.getUserName().equals(callerUsername)) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.FORBIDDEN, "No puedes acceder a este usuario"),
                    null,
                    List.of(new ApiError("ACCESS_DENIED", null, "No puedes acceder a este usuario"))
            );
        }

        if (user.isVerified()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.CONFLICT, "El correo ya está verificado"),
                    null,
                    List.of(new ApiError("ACCOUNT_ALREADY_VERIFIED", null, "El correo ya está verificado"))
            );
        }

        String token = RandomTokenUtils.randomToken(user.getUserName());
        emailVerificationService.saveVerificationRequest(user, token);
        sendRecoveryEmail(user, "email-verification", EmailList.EMAIL_VERIFICATION, token);

        return errorFactory.buildSuccess(HttpStatus.OK, "Email de verificación enviado exitosamente");
    }

    private void sendRecoveryEmail(User user, String pathSegment, EmailList template, String token) {
        String link = LinkBuilderUtils.buildEmailLink(domain, pathSegment, user.getId());
        MailUtils.sendEmail(mailSender, emailUser, user.getEmail(), template.getSubject(), template.getMessage(user.getUserName(), link));
    }

    @Override
    public void sendEmail(String toUser, String subject, String message) {
        MailUtils.sendEmail(mailSender, emailUser, toUser, subject, message);
    }

    @Override
    public void sendEmailWithFile(String toUser, String subject, String message, File file) {
        MailUtils.sendEmailWithFile(mailSender, emailUser, toUser, subject, message, file);
    }

    @Transactional
    public ApiResponse<Void> accountRecovery(Long id, String token, String newPassword) {
        var validation = accountRecoveryService.validateToken(id, token);
        if (validation.meta().status() != HttpStatus.OK.value()) {
            return new ApiResponse<>(validation.meta(), null, validation.errors());
        }

        PasswordUtils.validatePassword(newPassword);
        String encoded = PasswordUtils.encode(newPassword, userService.getPasswordEncoder());
        userService.updatePassword(encoded, id);

        return errorFactory.buildSuccess(HttpStatus.OK, "Contraseña actualizada y cuenta recuperada exitosamente");
    }

    @Transactional
    public ApiResponse<Void> emailVerification(Long id, String token) {
        var validation = emailVerificationService.validateToken(id, token);
        if (validation.meta().status() != HttpStatus.OK.value()) {
            return new ApiResponse<>(validation.meta(), null, validation.errors());
        }

        userService.markAsVerified(id);
        return errorFactory.buildSuccess(HttpStatus.OK, "Correo verificado exitosamente");
    }


}

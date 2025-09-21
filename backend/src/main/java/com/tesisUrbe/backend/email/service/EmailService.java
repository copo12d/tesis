package com.tesisUrbe.backend.email.service;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.NormalizationUtils;
import com.tesisUrbe.backend.common.util.PasswordUtils;
import com.tesisUrbe.backend.common.util.RandomTokenUtils;
import com.tesisUrbe.backend.email.dto.UserRecoveryDto;
import com.tesisUrbe.backend.email.enums.EmailList;
import com.tesisUrbe.backend.email.model.AccountRecovery;
import com.tesisUrbe.backend.email.model.EmailVerification;
import com.tesisUrbe.backend.email.model.PasswordRecovery;
import com.tesisUrbe.backend.email.repository.EmailVerificationRepository;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.email.repository.AccountRecoveryRepository;
import com.tesisUrbe.backend.email.repository.PasswordRecoveryRepository;
import com.tesisUrbe.backend.users.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService{

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final AccountRecoveryRepository accountRecoveryRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final ApiErrorFactory errorFactory;
    private final PasswordEncoder passwordEncoder;

    @Value("${EMAIL_USER}")
    private String emailUser;

    @Value("${api.base-path}")
    private String domain;

    @Autowired
    private JavaMailSender mailSender;

    // Post Services
    @Transactional
    public ApiResponse<Void> passwordRecoveryRequest(UserRecoveryDto userRecoveryDto){
        User user = userExists(userRecoveryDto);

        if(user == null){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        String token = RandomTokenUtils.randomToken(user.getUserName());
        PasswordRecovery passwordRecovery = PasswordRecovery.builder()
                .user(user)
                .recovery_token(token)
                .build();

        passwordRecoveryRepository.save(passwordRecovery);

        String link = UriComponentsBuilder.fromUriString(domain)
                    .path("/email/public/password-recovery/" + user.getId() + "/token")
                    .toUriString();

        sendEmail(user.getEmail(),
                    EmailList.PASSWORD_RECOVERY.getSubject(),
                    EmailList.PASSWORD_RECOVERY.getMessage(
                            user.getUserName(),
                            link
                    )
            );

        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Email de recuperación de contraseña enviado exitosamente"
        );

    }

    @Transactional
    public ApiResponse<Void> accountRecoveryRequest(UserRecoveryDto userRecoveryDto){
        User user = userExists(userRecoveryDto);

        if(user == null){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        if(!user.isAccountLocked()){
            return errorFactory.<Void>build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("ACCOUNT_NO_LOCKED", null, "La cuenta no esta bloqueada"))
            );
        }

        String token = RandomTokenUtils.randomToken(user.getUserName());
        AccountRecovery accountRecovery = AccountRecovery.builder()
                .user(user)
                .recovery_token(token)
                .build();

        accountRecoveryRepository.save(accountRecovery);

        String link = UriComponentsBuilder.fromUriString(domain)
                .path("/email/public/account-recovery/" + user.getId() + "/token")
                .toUriString();

        sendEmail(user.getEmail(),
                EmailList.ACCOUNT_RECOVERY.getSubject(),
                EmailList.ACCOUNT_RECOVERY.getMessage(
                        user.getUserName(),
                        link
                )
        );
        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Email de recuperación de cuenta enviado exitosamente"
        );
    }

    @Transactional
    public ApiResponse<Void> emailVerificationRequest(Long id){
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
                    List.of(new ApiError(
                            "USER_NOT_FOUND",
                            null,
                            "Usuario no público o no encontrado"))
            );
        }

        if (!user.getUserName().equals(callerUsername)) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "ACCESS_DENIED",
                            null,
                            "No puedes acceder a la información de este usuario"))
            );
        }

        if (user.isVerified()){
            return errorFactory.<Void>build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError(
                            "ACCOUNT_ALREADY_VERIFIED",
                            null,
                            "El correo de esta cuenta ya esta verificado"))
            );
        }

        String token = RandomTokenUtils.randomToken(user.getUserName());
        EmailVerification emailVerification = EmailVerification.builder()
                .user(user)
                .verification_token(token)
                .build();

        emailVerificationRepository.save(emailVerification);
            String link = UriComponentsBuilder.fromUriString(domain)
                    .path("/email/public/email-verification/"+ user.getId() +"/token")
                    .toUriString();



        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Email de recuperación de cuenta enviado exitosamente"
        );
    }

    //Put Services
    @Transactional
    public ApiResponse<Void> passwordRecovery(Long id, String token,String newPassword){

        PasswordRecovery request = passwordRecoveryRepository.findLatestByUserId(id);

        if (request == null){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "PASSWORD_RECOVERY_REQUEST_NOT_FOUND",
                            null,
                            "No se ha encontrado ninguna solicitud de recuperacion" +
                                    "de contraseña para el usuario"))
            );
        }

        if (!request.getRecovery_token().equals(token)){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "INVALID_TOKEN",
                            null,
                            "Token de solicitud no valido"))
            );
        }

        if (LocalDateTime.now().isAfter(request.getExpiration_date())){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                                "EXPIRED_TOKEN",
                            null,
                            "Token de solicitud vencido"))
            );
        }

        if (!userRepository.existsById(id)){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "USER_NOT_FOUND",
                            null,
                            "Usuario no encontrado"))
            );
        }

        PasswordUtils.validatePassword(newPassword);

        userRepository.updatePassword(passwordEncoder.encode(newPassword), id);

        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Nueva contraseña para la cuenta creada exitosamente"
        );
    }

    @Transactional
    public ApiResponse<Void> accountRecovery (Long id, String token, String newPassword){

        AccountRecovery request = accountRecoveryRepository.findLatestByUserId(id);

        if (userRepository.isLockedUserById(id)){
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError(
                            "ACCOUNT_NOT_LOCKED",
                            null,
                            "El usuario no esta bloqueado por exceso de intentos de contraseñas"))
            );
        }

        if (request == null){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "ACCOUNT_RECOVERY_REQUEST_NOT_FOUND",
                            null,
                            "No se ha encontrado ninguna solicitud de recuperacion" +
                                    "de cuenta para el usuario"))
            );
        }

        if (!request.getRecovery_token().equals(token)){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "INVALID_TOKEN",
                            null,
                            "Token de solicitud no valido"))
            );
        }

        if (LocalDateTime.now().isAfter(request.getExpiration_date())){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "EXPIRED_TOKEN",
                            null,
                            "Token de solicitud vencido"))
            );
        }

        if (request.isUsed()){
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError(
                            "USED_TOKEN",
                            null,
                            "Token de solicitud usado"))
            );
        }

        if (!userRepository.existsById(id)){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "USER_NOT_FOUND",
                            null,
                            "Usuario no encontrado"))
            );
        }

        PasswordUtils.validatePassword(newPassword);

        userRepository.updatePassword(passwordEncoder.encode(newPassword), id);

        accountRecoveryRepository.markAsUsed(id);

        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Cuenta desbloqueada existosamente"
        );
    }

    @Transactional
    public ApiResponse<Void> emailVerification(Long id, String token){

        if (userRepository.isVerifiedUserById(id)){
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError(
                            "ACCOUNT_ALREADY_VERIFIED",
                            null,
                            "El correo del usuario ya esta verificado"))
            );
        }

        EmailVerification request = emailVerificationRepository.findLatestByUserId(id);

        if (request == null){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "EMAIL_VERIFICATION_REQUEST_NOT_FOUND",
                            null,
                            "No se ha encontrado ninguna solicitud de verificacion" +
                                    "de email para el usuario"))
            );
        }

        if (!request.getVerification_token().equals(token)){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "INVALID_TOKEN",
                            null,
                            "Token de solicitud no valido"))
            );
        }

        if (LocalDateTime.now().isAfter(request.getExpiration_date())){
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError(
                            "EXPIRED_TOKEN",
                            null,
                            "Token de solicitud vencido"))
            );
        }

        if (!userRepository.existsById(id)){
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError(
                            "USER_NOT_FOUND",
                            null,
                            "Usuario no encontrado"))
            );
        }

        userRepository.verifiedUserEmail(id);

        return errorFactory.buildSuccess(
                HttpStatus.OK,
                "Email verificado existosamente"
        );
    }

    //Private Functions
    private User userExists(UserRecoveryDto userRecovery){
        userRecovery.setUserName(NormalizationUtils.normalizeUsername(userRecovery.getUserName()));
        userRecovery.setEmail(NormalizationUtils.normalizeEmail(userRecovery.getEmail()));

        if (userRecovery.getUserName() == null && userRecovery.getEmail() == null) {
            throw new IllegalArgumentException("Ningun parametro de busqueda ingresado");
        }
        User user;
        if (userRecovery.getUserName() != null) {
            user = userRepository.findByUserName(userRecovery.getUserName())
                    .orElse(null);

        } else {
            user = userRepository.findByEmail(userRecovery.getEmail())
                    .orElse(null);
        }

        return user;
    }


    @Override
    public void sendEmail(String toUser, String subject, String message){
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name()
            );

            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendEmailWithFile(String toUser, String subject, String message, File file) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name()
            );

            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}

package com.tesisUrbe.backend.email.service;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.email.model.EmailVerification;
import com.tesisUrbe.backend.email.repository.EmailVerificationRepository;
import com.tesisUrbe.backend.users.dto.UserRecovery;
import com.tesisUrbe.backend.email.enums.EmailList;
import com.tesisUrbe.backend.users.exceptions.InvalidUserDataException;
import com.tesisUrbe.backend.users.model.AccountRecovery;
import com.tesisUrbe.backend.users.model.PasswordRecovery;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.AccountRecoveryRepository;
import com.tesisUrbe.backend.users.repository.PasswordRecoveryRepository;
import com.tesisUrbe.backend.users.repository.UserRepository;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class EmailService implements IEmailService{

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final AccountRecoveryRepository accountRecoveryRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${EMAIL_USER}")
    private String emailUser;

    @Value("${DOMAIN}")
    private String domain;

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(UserService userService, UserRepository userRepository, AuthService authService, PasswordRecoveryRepository passwordRecoveryRepository, AccountRecoveryRepository accountRecoveryRepository, EmailVerificationRepository emailVerificationRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordRecoveryRepository = passwordRecoveryRepository;
        this.accountRecoveryRepository = accountRecoveryRepository;
        this.emailVerificationRepository = emailVerificationRepository;
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

    public void passwordRecovery(UserRecovery userRecovery){
        User user = this.userExists(userRecovery);
        UserUtils.checkBlock(user);
        try {
            String token = authService.randomToken();
            PasswordRecovery passwordRecovery = new PasswordRecovery(user, token);

            passwordRecoveryRepository.save(passwordRecovery);
            String link = UriComponentsBuilder.fromUriString(domain)
                    .path("/users/password-recovery/"+ user.getId())
                    .queryParam("token", token)
                    .toUriString();

            sendEmail(user.getEmail(),
                    EmailList.PASSWORD_RECOVERY.getSubject(),
                    EmailList.PASSWORD_RECOVERY.getMessage(
                            user.getUserName(),
                            link
                    )
            );

        }  catch (Exception e) {
            throw new RuntimeException(
                    "Ocurrió un error inesperado al tratar de recuperar la contraseña." +
                            e.getMessage(), e
            );
        }
    }

    public void accountRecovery(UserRecovery userRecovery){
        User user = this.userExists(userRecovery);
        UserUtils.checkActive(user);
        if (!user.isBlocked()){
            throw new InvalidUserDataException("Usuario no bloqueado");
        }
        try{

            String token = authService.randomToken();
            AccountRecovery accountRecovery = new AccountRecovery(user, token);

            accountRecoveryRepository.save(accountRecovery);
            String link = UriComponentsBuilder.fromUriString(domain)
                    .path("/users/account-recovery/"+ user.getId())
                    .queryParam("token", token)
                    .toUriString();
            sendEmail(user.getEmail(),
                    EmailList.ACCOUNT_RECOVERY.getSubject(),
                    EmailList.ACCOUNT_RECOVERY.getMessage(
                            user.getUserName(),
                            link
                    )
            );
        }catch (Exception e){
            throw new RuntimeException(
                    "Ocurrió un error inesperado al tratar de recuperar la cuenta." +
                            e.getMessage(), e
            );
        }

    }

    public void emailVerification(Authentication authentication){
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado"));

        UserUtils.checkBlock(user);

        if (user.isVerified()){
            throw new AccessDeniedException("Correo ya verificado");
        }

        try{
            String token = authService.randomToken();
            EmailVerification emailVerification = new EmailVerification(user, token);

            emailVerificationRepository.save(emailVerification);
            String link = UriComponentsBuilder.fromUriString(domain)
                    .path("/email-request/verified/"+ user.getId())
                    .queryParam("token", token)
                    .toUriString();

            sendEmail(user.getEmail(),
                    EmailList.EMAIL_VERIFICATION.getSubject(),
                    EmailList.EMAIL_VERIFICATION.getMessage(
                            user.getUserName(),
                            user.getEmail(),
                            link
                    )
            );

        }catch (Exception e){
            throw new RuntimeException(
                    "Ocurrió un error inesperado al tratar de verificar el correo de la cuenta." +
                            e.getMessage(), e
            );
        }
    }

    public void verified(Long id, String token){
        EmailVerification emailVerification = emailVerificationRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!Objects.equals(emailVerification.getVerification_token(), token)){
            throw new AccessDeniedException("No tiene el acceso a esta operacion");
        }
        if (LocalDateTime.now().isAfter(emailVerification.getExpiration_date())){
            throw new AccessDeniedException("Token Vencido, Solicite otro");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        UserUtils.checkBlock(user);

        user.setVerified(true);
        userRepository.save(user);

    }

    private User userExists(UserRecovery userRecovery){
        if (userRecovery.getUserName() == null && userRecovery.getEmail() == null) {
            throw new IllegalArgumentException("Ningun parametro de busqueda ingresado");
        }
        User user;
        if (userRecovery.getUserName() != null) {
            user = userRepository.findByUserName(userRecovery.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        } else {
            user = userRepository.findByEmail(userRecovery.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        }

        return user;
    }


}

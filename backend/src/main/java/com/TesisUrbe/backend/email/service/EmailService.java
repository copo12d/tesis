package com.tesisUrbe.backend.email.service;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.email.dto.UserPassRecovery;
import com.tesisUrbe.backend.email.enums.EmailList;
import com.tesisUrbe.backend.users.model.PasswordRecovery;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.PasswordRecoveryRepostory;
import com.tesisUrbe.backend.users.repository.UserRepository;
import com.tesisUrbe.backend.users.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService implements IEmailService{

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordRecoveryRepostory passwordRecoveryRepostory;

    @Value("${EMAIL_USER}")
    private String emailUser;

    @Value("${DOMAIN}")
    private String domain;

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(UserService userService, UserRepository userRepository, AuthService authService, PasswordRecoveryRepostory passwordRecoveryRepostory) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordRecoveryRepostory = passwordRecoveryRepostory;
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

    public void PasswordRecovery(UserPassRecovery userPassRecovery){
        if (userPassRecovery.getUserName() == null && userPassRecovery.getEmail() == null) {
            throw new IllegalArgumentException("Ningun parametro de busqueda ingresado");
        }
        User user;
        if (userPassRecovery.getUserName() != null) {
            user = userRepository.findByUserName(userPassRecovery.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        } else {
            user = userRepository.findByEmail(userPassRecovery.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        }

        try {
            String token = authService.randomToken();
            PasswordRecovery passwordRecovery = new PasswordRecovery(user, token);

            passwordRecoveryRepostory.save(passwordRecovery);
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
            throw new RuntimeException("Ocurrió un error inesperado al tratar de recuperar la contraseña." + e.getMessage(), e);
        }
    }
}

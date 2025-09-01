package com.tesisUrbe.backend.email.model;

import com.tesisUrbe.backend.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "email_verification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String verification_token;

    @Column(nullable = false)
    private LocalDateTime expiration_date;

    public EmailVerification(User user, String verification_token){
        this.user = user;
        this.verification_token = verification_token;
        this.expiration_date = LocalDateTime.now().plusHours(24);
    }
}

package com.tesisUrbe.backend.email.model;

import com.tesisUrbe.backend.entities.account.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_recoverys")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordRecovery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String recovery_token;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime expiration_date = LocalDateTime.now().plusHours(24);

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

}

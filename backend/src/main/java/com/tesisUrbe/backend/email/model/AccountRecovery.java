package com.tesisUrbe.backend.email.model;

import com.tesisUrbe.backend.entities.account.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_recoverys")
@Builder
public class AccountRecovery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String recovery_token;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime expiration_date = LocalDateTime.now().plusHours(24);

    @Builder.Default
    @Column(nullable = false)
    private boolean used = false;
}
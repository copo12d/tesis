package com.tesisUrbe.backend.users.model;

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
@Table(name = "account_recoverys")
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

    @Column(nullable = false)
    private LocalDateTime expiration_date;

    @Column(nullable = false)
    private boolean used = false;

    public AccountRecovery(User user, String recovery_token){
        this.user = user;
        this.recovery_token = recovery_token;
        this.expiration_date = LocalDateTime.now().plusHours(24);
        this.used = false;
    }
}

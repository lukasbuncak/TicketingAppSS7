package nl.fontys.s7.ticketingapp.persistance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "login_credentials", schema = "auth")
public class LoginCredentialEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @OneToOne
    @MapsId                           // <-- shares PK with UserEntity.id
    @JoinColumn(name = "user_id")     // FK = PK
    private UserEntity user;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull
    @Column(name = "last_password_change", nullable = false)
    private Instant lastPasswordChange;

    @PrePersist
    void onCreate() {
        if (lastPasswordChange == null) lastPasswordChange = Instant.now();
    }
}

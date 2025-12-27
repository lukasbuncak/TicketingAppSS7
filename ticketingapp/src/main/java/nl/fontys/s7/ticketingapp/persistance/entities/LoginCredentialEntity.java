package nl.fontys.s7.ticketingapp.persistance.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
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
    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull
    @Column(name = "last_password_change", nullable = false)
    private Instant lastPasswordChange;

    @NotNull
    @Column(name = "totp_enabled", nullable = false)
    private Boolean totpEnabled = false;

    @NotNull
    @Column(name = "totp_pending", nullable = false)
    private Boolean totpPending = false;

    @JsonIgnore
    @Column(name = "totp_secret")
    private String totpSecret;

    @PrePersist
    void onCreate() {
        if (lastPasswordChange == null) lastPasswordChange = Instant.now();
    }
}

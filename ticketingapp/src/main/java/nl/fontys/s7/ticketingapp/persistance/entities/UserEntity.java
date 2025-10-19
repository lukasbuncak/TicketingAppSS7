package nl.fontys.s7.ticketingapp.persistance.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "core")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false,
            cascade = CascadeType.ALL, orphanRemoval = true) // <-- handy for create/remove
    private LoginCredentialEntity credentials;

    @NotBlank @Email
    @Column(name = "personal_email", nullable = false, updatable = false)
    private String personalEmail;

    @NotBlank
    @Email
    @Column(name = "school_email", nullable = false, updatable = false)
    private String schoolEmail;

    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Enumerated(EnumType.STRING)               // maps to core.user_status enum values
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "disabled_at")
    private Instant disabledAt;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

}

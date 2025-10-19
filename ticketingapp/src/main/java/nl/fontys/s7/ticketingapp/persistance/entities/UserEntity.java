package nl.fontys.s7.ticketingapp.persistance.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "users",
        schema = "core",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_personal_email", columnNames = "personal_email"),
                @UniqueConstraint(name = "uq_users_school_email",   columnNames = "school_email")
        }
)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore                 // prevent recursion in JSON
    private LoginCredentialEntity credentials;

    @NotBlank @Email
    @Column(name = "personal_email", nullable = false, updatable = false)
    private String personalEmail;

    // Generated column in DB -> read-only from JPA, no @NotBlank
    @Email
    @Column(name = "school_email", nullable = false, updatable = false, insertable = false)
    private String schoolEmail;

    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "disabled_at")
    private Instant disabledAt;

    // If you don’t want Hibernate annotations, keep your @PrePersist/@PreUpdate instead.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}

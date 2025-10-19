package nl.fontys.s7.ticketingapp.persistance.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments_meta", schema = "tickets")
public class AttachmentMetaEntity {
    public enum ScanStatus { PENDING, CLEAN, QUARANTINED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    @NotBlank
    @Column(name = "blob_path", nullable = false, length = 512)
    private String blobPath;

    @NotBlank
    @Size(max = 500)
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @NotBlank
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @PositiveOrZero
    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @NotNull
    @Enumerated(EnumType.STRING)                 // mirrors DB CHECK constraint
    @Column(name = "scan_status", nullable = false, length = 16)
    private ScanStatus scanStatus = ScanStatus.PENDING;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}

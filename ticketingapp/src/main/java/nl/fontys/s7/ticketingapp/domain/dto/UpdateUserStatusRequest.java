package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;

public record UpdateUserStatusRequest(
        @NotNull String status,
        @NotNull Integer userId,
        String reason // optional, for logs/audit later
) {
}

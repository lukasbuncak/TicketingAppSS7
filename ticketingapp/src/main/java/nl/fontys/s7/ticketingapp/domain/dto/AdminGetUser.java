package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.NotNull;

public record AdminGetUser(@NotNull String schoolEmail,
                           @NotNull String displayName,
                           @NotNull String status,
                           @NotNull String PersonalId) {
}

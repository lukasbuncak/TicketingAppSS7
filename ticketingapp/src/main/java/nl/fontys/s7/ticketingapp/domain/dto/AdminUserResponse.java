package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserResponse(

        @NotNull String schoolEmail,
        @NotNull String displayName,
        @NotNull String status,
        String TempPassword
) {}
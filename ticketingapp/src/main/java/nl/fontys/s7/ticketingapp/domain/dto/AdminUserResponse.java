package nl.fontys.s7.ticketingapp.domain.dto;

public record AdminUserResponse(

        String schoolEmail,
        String displayName,
        String status
) {}
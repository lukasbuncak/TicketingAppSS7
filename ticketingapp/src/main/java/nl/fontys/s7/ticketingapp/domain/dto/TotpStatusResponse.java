package nl.fontys.s7.ticketingapp.domain.dto;

public record TotpStatusResponse(
        boolean enabled,
        boolean pending
) {}

package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String schoolEmail,
        @NotBlank String password
) {}
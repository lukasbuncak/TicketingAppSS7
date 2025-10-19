package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserAccountRequest(
        @NotBlank @Email String personalEmail,
        @NotBlank String displayName
) {}

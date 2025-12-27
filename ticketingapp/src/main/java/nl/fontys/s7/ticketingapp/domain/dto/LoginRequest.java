package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank @Email String schoolEmail,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z]).{8,64}$",
                message = "Password must contain at least one uppercase letter and one number"
        )
        String password
) {}
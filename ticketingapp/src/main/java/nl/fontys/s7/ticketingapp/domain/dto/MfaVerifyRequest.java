package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaVerifyRequest(@NotBlank String code) {
}

package nl.fontys.s7.ticketingapp.domain.dto;

public sealed interface AuthResult permits AuthResult.Success, AuthResult.MfaRequired {
    record Success(String accessToken) implements AuthResult {}
    record MfaRequired(String mfaToken) implements AuthResult {}
}

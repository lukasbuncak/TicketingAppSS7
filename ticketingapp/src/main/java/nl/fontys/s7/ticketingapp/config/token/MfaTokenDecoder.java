package nl.fontys.s7.ticketingapp.config.token;

public interface MfaTokenDecoder {
    MfaToken decode(String tokenEncoded);
}

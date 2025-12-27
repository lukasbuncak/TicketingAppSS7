package nl.fontys.s7.ticketingapp.config.token;

public interface MfaTokenEncoder {
    String encode(MfaToken token);
}

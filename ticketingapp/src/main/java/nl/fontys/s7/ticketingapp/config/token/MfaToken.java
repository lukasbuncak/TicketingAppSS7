package nl.fontys.s7.ticketingapp.config.token;

public interface MfaToken {
    String getSubject();
    Integer getUserId();
}

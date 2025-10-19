package nl.fontys.s7.ticketingapp.config.token;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}

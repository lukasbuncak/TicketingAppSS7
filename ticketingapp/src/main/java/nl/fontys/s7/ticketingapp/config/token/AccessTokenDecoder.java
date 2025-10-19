package nl.fontys.s7.ticketingapp.config.token;

public interface AccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}

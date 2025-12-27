package nl.fontys.s7.ticketingapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BearerTokenExtractor {
    public String extract ( String header ) {
        if (header == null || ! header.startsWith ( "Bearer " )) {
            throw new ResponseStatusException ( HttpStatus.UNAUTHORIZED, "Missing Bearer token" );
        }
        return header.substring ( "Bearer ".length ( ) ).trim ( );
    }
}

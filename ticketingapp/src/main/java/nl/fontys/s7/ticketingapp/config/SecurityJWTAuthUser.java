// src/main/java/nl/fontys/s7/ticketingapp/config/SecurityJWTAuthUser.java
package nl.fontys.s7.ticketingapp.config;

import nl.fontys.s7.ticketingapp.config.token.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class SecurityJWTAuthUser {
    private SecurityJWTAuthUser() {}

    /** Read the authenticated userId that your filter put into the principal's username. */
    public static Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
        }

        Object principal = auth.getPrincipal();

        // Your filter sets: new User(String.valueOf(at.getUserId()), "", authorities)
        if (principal instanceof User u) {
            try {
                return Integer.valueOf(u.getUsername());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid principal");
            }
        }

        // Fallback: if someone later changes the principal to the token itself
        if (principal instanceof AccessToken at) {
            return at.getUserId();
        }

        // As a last resort, try auth.getDetails() where you stored the token
        Object details = auth.getDetails();
        if (details instanceof AccessToken at) {
            return at.getUserId();
        }

        throw new ResponseStatusException(UNAUTHORIZED, "Unknown principal");
    }

    /** Optional convenience: read the role you stored in the token. */
    public static String currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof AccessToken at) {
            return at.getRoles();
        }
        // Or derive from authorities (they’re already prefixed with ROLE_)
        return auth.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse(null);
    }

    /** Optional: expose the raw AccessToken you attached as details. */
    public static AccessToken currentToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object details = auth.getDetails();
        return (details instanceof AccessToken at) ? at : null;
    }
}

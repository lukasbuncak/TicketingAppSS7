package nl.fontys.s7.ticketingapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.exception.InvalidAccessTokenException;
import nl.fontys.s7.ticketingapp.config.token.AccessToken;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthenticationRequestFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";
    private final AccessTokenDecoder accessTokenDecoder;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // These paths are already permitAll() in SecurityConfig
    private static final String[] PUBLIC_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/actuator/health",
            "/actuator/info",
            "/auth/login"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // CORS preflight should not require JWT
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        for (String pattern : PUBLIC_PATHS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;   // skip JWT logic for these
            }
        }
        return false;          // run JWT logic for everything else
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        // Read "Authorization: Bearer <jwt>"
        final String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response); // no token -> continue (endpoint may be public)
            return;
        }

        final String tokenValue = auth.substring(7);

        try {
            // Decode & validate (signature + exp + issuer + audience)
            AccessToken at = accessTokenDecoder.decode(tokenValue);

            // Single role -> one authority
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(ROLE_PREFIX + at.getRoles());

            // Put a minimal principal into the SecurityContext.
            // We store userId in username to keep it handy.
            UserDetails principal = new User (String.valueOf(at.getUserId()), "", List.of(authority));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            authToken.setDetails(at); // keep the raw AccessToken handy if you want to read claims later

            SecurityContextHolder.getContext().setAuthentication(authToken);

            chain.doFilter(request, response);
        } catch (InvalidAccessTokenException e) {
            // Invalid token -> 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        }
    }
}
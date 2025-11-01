package nl.fontys.s7.ticketingapp.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.AuthService;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenEncoder;
import nl.fontys.s7.ticketingapp.config.token.impl.AccessTokenImpl;
import nl.fontys.s7.ticketingapp.domain.dto.LoginRequest;
import nl.fontys.s7.ticketingapp.domain.dto.LoginResponse;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;
import nl.fontys.s7.ticketingapp.persistance.entities.LoginCredentialEntity;
import nl.fontys.s7.ticketingapp.persistance.entities.UserEntity;
import nl.fontys.s7.ticketingapp.persistance.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final Argon2PasswordEncoder argon2;
    private final AccessTokenEncoder jwt; // your encoder that sets issuer/audience/exp
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);
    @Override
    public LoginResponse login( LoginRequest request) {
        // 1) Lookup
        UserEntity user = users.findBySchoolEmail (request.schoolEmail ())
                .orElseThrow(AuthServiceImpl::invalid);

        // 2) Basic status checks
        if (user.getStatus() != UserStatus.ACTIVE || user.getCredentials() == null) {
            throw invalid();
        }


        // 3) Verify password with Argon2 (salt is inside the stored hash)
        LoginCredentialEntity cred = user.getCredentials();
        if (cred == null) {
            log.warn("Login: no credentials for userId={}, schoolEmail={}", user.getId(), user.getSchoolEmail());
            throw invalid();
        }

        // do the comparison in a local var, so we can log it
        boolean matches = argon2.matches(request.password(), cred.getPasswordHash());

        // minimal, non-sensitive logging
        log.debug("Login compare: userId={}, schoolEmail={}, hashPresent={}, pwdLen={}, match={}",
                user.getId(),
                user.getSchoolEmail(),
                cred.getPasswordHash() != null,
                request.password() == null ? null : request.password().length(),
                matches
        );

        if (!argon2.matches(request.password(), cred.getPasswordHash())) {
            System.out.println(cred.getPasswordHash() + "my db hash");
            // (Optional) implement login-attempt accounting/lockout elsewhere
            throw invalid();
        }

        // 4) Issue short-lived JWT (role is a single string in your design)
        String role = "STUDENT"; // or read from user if you store it
        String token = jwt.encode(new AccessTokenImpl (
                user.getDisplayName (),    // subject (could also be schoolEmail)
                user.getId(),               // userId claim
                role                        // role claim
        ));

        return new LoginResponse(token);
    }

    private static RuntimeException invalid() {
        // Generic to avoid user enumeration
        return new RuntimeException("Invalid email or password");
    }
}

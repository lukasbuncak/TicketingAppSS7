package nl.fontys.s7.ticketingapp.business.impl;

import jakarta.transaction.Transactional;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;
import nl.fontys.s7.ticketingapp.persistance.repository.LoginCredentialRepository;
import nl.fontys.s7.ticketingapp.persistance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl {

//    private final UserRepository users;
//    private final LoginCredentialRepository creds;
//    private final PasswordEncoder encoder; // BCryptPasswordEncoder or Argon2PasswordEncoder
//    private final JwtIssuer jwt;           // your RS256 issuer
//
//    public AuthServiceImpl ( ) {
//    }
//
//    @Transactional(readOnly = true)
//    public String login(String email, String rawPassword) {
//        var user = users.findByPersonalEmail(email)
//                .orElseThrow(() -> unauthorized());
//
//        if (user.getStatus() != UserStatus.ACTIVE) throw unauthorized();
//
//        var cred = creds.findById(user.getId())
//                .orElseThrow(() -> unauthorized());
//
//        if (!encoder.matches(rawPassword, cred.getPasswordHash())) {
//            // increment failure counter / rate-limit (store outside credentials table)
//            throw unauthorized();
//        }
//
//        // Optional: compare JWT iat with cred.getLastPasswordChange() to force re-login after change
//        return jwt.issue(user.getId(), /* roles/claims */);
//    }
}

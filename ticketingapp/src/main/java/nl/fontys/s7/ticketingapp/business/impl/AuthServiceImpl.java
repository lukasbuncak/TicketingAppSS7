package nl.fontys.s7.ticketingapp.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.AuthService;
import nl.fontys.s7.ticketingapp.config.TotpVerifier;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenEncoder;
import nl.fontys.s7.ticketingapp.config.token.MfaToken;
import nl.fontys.s7.ticketingapp.config.token.MfaTokenDecoder;
import nl.fontys.s7.ticketingapp.config.token.MfaTokenEncoder;
import nl.fontys.s7.ticketingapp.config.token.impl.AccessTokenImpl;
import nl.fontys.s7.ticketingapp.config.token.impl.MfaTokenImpl;
import nl.fontys.s7.ticketingapp.domain.dto.AuthResult;
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
    private final MfaTokenDecoder mfaDecodeJwt;
    private final MfaTokenEncoder mfaEncodeJwt;
    private final TotpVerifier totpVerifier;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);
    @Override
    @Transactional
    public AuthResult login( LoginRequest request) {
        // 1) Lookup
        UserEntity user = users.findBySchoolEmail (request.schoolEmail ())
                .orElseThrow(AuthServiceImpl::invalid);

        // 2) Basic status checks
        if (user.getStatus() != UserStatus.ACTIVE || user.getCredentials() == null) {
            throw invalid();
        }


        // 3) Verify password with Argon2 (salt is inside the stored hash)
        LoginCredentialEntity cred = user.getCredentials();


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

        if (Boolean.TRUE.equals(cred.getTotpEnabled())) {
            String mfaToken = mfaEncodeJwt.encode(new MfaTokenImpl (
                    user.getDisplayName(),
                    user.getId()
            ));
            return new AuthResult.MfaRequired(mfaToken);
        }

        // 4) Issue short-lived JWT (role is a single string in your design)
        String role = "STUDENT"; // or read from user if you store it
        String token = jwt.encode(new AccessTokenImpl (
                user.getDisplayName (),    // subject (could also be schoolEmail)
                user.getId(),               // userId claim
                role                        // role claim
        ));

        return new AuthResult.Success(token);
    }

    @Override
    public LoginResponse verifyMfa(String mfaTokenEncoded, String code) {
        // 1) Validate MFA token (signature + exp + issuer/aud + mfa=true)
        MfaToken mfaToken = mfaDecodeJwt.decode(mfaTokenEncoded);
        Integer userId = mfaToken.getUserId();

        // 2) Load user + creds
        UserEntity user = users.findById(userId).orElseThrow(AuthServiceImpl::invalid);
        LoginCredentialEntity cred = user.getCredentials();
        if (cred == null || !Boolean.TRUE.equals(cred.getTotpEnabled())) {
            // Either MFA not enabled or credentials missing → reject
            throw invalid();
        }

        // 3) Verify TOTP
        if (!totpVerifier.verify(cred.getTotpSecret(), code)) {
            throw invalidMfa(); // separate message if you want, but keep generic for enumeration safety
        }

        // 4) Issue real access token
        String accessToken = issueAccessToken(user);
        return new LoginResponse(accessToken);
    }

    private String issueAccessToken(UserEntity user) {
        String role = "STUDENT"; // or load from your user model
        return jwt.encode(new AccessTokenImpl(
                user.getDisplayName(),
                user.getId(),
                role
        ));
    }

    private static RuntimeException invalidMfa() {
        return new RuntimeException("Invalid MFA code");
    }

    private static RuntimeException invalid() {
        // Generic to avoid user enumeration
        return new RuntimeException("Invalid email or password");
    }
}

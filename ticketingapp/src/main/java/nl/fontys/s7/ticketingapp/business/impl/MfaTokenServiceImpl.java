package nl.fontys.s7.ticketingapp.business.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fontys.s7.ticketingapp.business.MfaTokenService;
import nl.fontys.s7.ticketingapp.config.token.AccessToken;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenDecoder;
import nl.fontys.s7.ticketingapp.domain.dto.TotpSetupResponse;
import nl.fontys.s7.ticketingapp.domain.dto.TotpStatusResponse;
import nl.fontys.s7.ticketingapp.persistance.entities.LoginCredentialEntity;
import nl.fontys.s7.ticketingapp.persistance.entities.UserEntity;
import nl.fontys.s7.ticketingapp.persistance.repository.LoginCredentialRepository;
import nl.fontys.s7.ticketingapp.persistance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MfaTokenServiceImpl implements MfaTokenService {

        private final UserRepository users;
        private final LoginCredentialRepository credentials;
        private final AccessTokenDecoder accessTokenDecoder;

        // issuer shown in authenticator UI
        private static final String ISSUER = "TicketsApp"; //TODO: Fix

        private final GoogleAuthenticator authenticator = new GoogleAuthenticator(
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                        .setTimeStepSizeInMillis(30_000)
                        .setWindowSize(3) // ±1 timestep
                        .build()
        );

        @Override
        @Transactional
        public TotpSetupResponse setupTotp( String bearerToken) {
            int userId = requireUserIdFromAccessToken(bearerToken);

            UserEntity user = users.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException ( HttpStatus.UNAUTHORIZED, "Invalid token user"));

            LoginCredentialEntity cred = requireCreds(user);

            if (Boolean.TRUE.equals(cred.getTotpEnabled())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "TOTP already enabled");
            }

//            if (Boolean.TRUE.equals(cred.getTotpPending())) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "TOTP already pending confirmation");
//            }

            // Generate per-user secret
            GoogleAuthenticatorKey key = authenticator.createCredentials();
            String secret = key.getKey();

            // Store secret as pending
            cred.setTotpSecret(secret);
            cred.setTotpPending(true);
            credentials.save ( cred );
            String accountName = user.getSchoolEmail();
            String label = ISSUER + ":" + accountName;

            // 2. Build the URI with %20 encoding for spaces
            String otpAuthUri = "otpauth://totp/"
                    + URLEncoder.encode(label, StandardCharsets.UTF_8).replace("+", "%20")
                    + "?secret=" + secret  // Note: 'secret' MUST be Base32 encoded
                    + "&issuer=" + URLEncoder.encode(ISSUER, StandardCharsets.UTF_8).replace("+", "%20")
                    + "&digits=6&period=30";

            return new TotpSetupResponse(otpAuthUri);
        }

        @Override
        @Transactional
        public void confirmTotp(String bearerToken, String code) {
            int userId = requireUserIdFromAccessToken(bearerToken);

            UserEntity user = users.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token user"));

            LoginCredentialEntity cred = requireCreds(user);

            if (!Boolean.TRUE.equals(cred.getTotpPending()) || cred.getTotpSecret() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TOTP setup not started");
            }

            if (code == null || !code.matches("\\d{6}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code format");
            }

            boolean ok = authenticator.authorize(cred.getTotpSecret(), Integer.parseInt(code));
            if (!ok) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid TOTP code");
            }

            // Confirm it
            cred.setTotpEnabled(true);
            cred.setTotpPending(false);

            credentials.save ( cred );
        }

    @Override
    @Transactional
    public TotpStatusResponse getTotpStatus(String bearerToken) {
        int userId = requireUserIdFromAccessToken(bearerToken);

        UserEntity user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token user"));

        LoginCredentialEntity cred = requireCreds(user);

        boolean enabled = Boolean.TRUE.equals(cred.getTotpEnabled());
        boolean pending = Boolean.TRUE.equals(cred.getTotpPending());

        return new TotpStatusResponse(enabled, pending);
    }

    //fully disable MFA for demo purposes
    @Override
    @Transactional
    public void disableTotp(String bearerToken) {
        int userId = requireUserIdFromAccessToken(bearerToken);

        UserEntity user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token user"));

        LoginCredentialEntity cred = requireCreds(user);

        cred.setTotpEnabled(false);
        cred.setTotpPending(false);
        cred.setTotpSecret(null);

        credentials.save(cred);
    }
    private int requireUserIdFromAccessToken(String bearerToken) {
            AccessToken token = accessTokenDecoder.decode(bearerToken);
            if (token.getUserId() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token missing userId");
            }
            return token.getUserId();
        }

        private LoginCredentialEntity requireCreds(UserEntity user) {
            if (user.getCredentials() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No credentials");
            }
            return user.getCredentials();
        }
}

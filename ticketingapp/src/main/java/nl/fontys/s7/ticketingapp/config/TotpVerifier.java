package nl.fontys.s7.ticketingapp.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.stereotype.Component;

@Component
public class TotpVerifier {

    private final GoogleAuthenticator authenticator;

    public TotpVerifier() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30_000)
                .setWindowSize(3) // ±1 step (recommended)
                .build();

        this.authenticator = new GoogleAuthenticator(config);
    }

    public boolean verify(String base32Secret, String code) {
        if (base32Secret == null || base32Secret.isBlank()) return false;
        if (code == null || !code.matches("\\d{6}")) return false;

        int otp = Integer.parseInt(code);
        return authenticator.authorize(base32Secret, otp);
    }
}
package nl.fontys.s7.ticketingapp.config.hashing;

import java.security.SecureRandom;

public final class PasswordUtil {
    private static final SecureRandom RNG = new SecureRandom();
    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*?-_";

    public static String generate() {
        int len = 16; // strong default
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private PasswordUtil() {}
}
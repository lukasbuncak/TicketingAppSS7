package nl.fontys.s7.ticketingapp.config.token.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import nl.fontys.s7.ticketingapp.business.exception.InvalidMfaTokenException;
import nl.fontys.s7.ticketingapp.config.token.MfaToken;
import nl.fontys.s7.ticketingapp.config.token.MfaTokenDecoder;
import nl.fontys.s7.ticketingapp.config.token.MfaTokenEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MfaTokenEncoderDecoderImpl implements MfaTokenDecoder, MfaTokenEncoder {

    private final Key key;
    private final String issuer;
    private final String audience;
    private final long expirationMinutes;

    public MfaTokenEncoderDecoderImpl(
            @Value("${jwt.mfa.secret}") String secretKey,
            @Value("${jwt.mfa.issuer}") String issuer,
            @Value("${jwt.mfa.audience:tickets-mfa}") String audience,
            @Value("${jwt.mfa.expiration-minutes:5}") long expirationMinutes
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.audience = audience;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public MfaToken decode ( String tokenEncoded ) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .setAllowedClockSkewSeconds(60)
                    .build();

            Jws < Claims > jws = parser.parseClaimsJws(tokenEncoded);
            Claims c = jws.getBody();

            // extra safety: ensure it's really an MFA token
            String typ = c.get("typ", String.class);
            if (!"mfa".equals(typ)) {
                throw new InvalidMfaTokenException ("Token is not an MFA token");
            }

            String subject = c.getSubject();
            Integer userId = c.get("userId", Integer.class);

            if (userId == null) {
                throw new InvalidMfaTokenException("Missing userId in MFA token");
            }

            return new MfaTokenImpl(subject, userId);
        } catch (JwtException e) {
            throw new InvalidMfaTokenException("Invalid MFA JWT", e);
        }
    }

    @Override
    public String encode ( MfaToken token ) {
        Map <String, Object> claimsMap = new HashMap <> ();
        claimsMap.put("userId", token.getUserId());
        claimsMap.put("typ", "mfa"); // distinguishes from access token

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(token.getSubject())
                .setIssuedAt( Date.from(now))
                .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .addClaims(claimsMap)
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(key)
                .compact();
    }
}


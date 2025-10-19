package nl.fontys.s7.ticketingapp.config.token.impl;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.fontys.s7.ticketingapp.business.exception.InvalidAccessTokenException;
import nl.fontys.s7.ticketingapp.config.token.AccessToken;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenDecoder;
import nl.fontys.s7.ticketingapp.config.token.AccessTokenEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccessTokenEncoderDecoderImpl implements AccessTokenEncoder, AccessTokenDecoder {
    private final Key key;
    private final String issuer;
    private final String audience;
    private final long expirationHours;

    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey,
                                         @Value("${jwt.issuer}") String issuer,
                                         @Value("${jwt.audience}") String audience,
                                         @Value("${jwt.access.expiration-hours:1}") long expirationHours) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.audience = audience;
        this.expirationHours = expirationHours;
    }
    @Override
    public AccessToken decode ( String accessTokenEncoded ) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .setAllowedClockSkewSeconds(60)
                    .build();

            Jws<Claims> jws = parser.parseClaimsJws(accessTokenEncoded);
            Claims c = jws.getBody();

            String subject = c.getSubject();
            Integer userId = c.get("userId", Integer.class);
            String role = c.get("role", String.class);

            return new AccessTokenImpl(subject, userId, role);
        } catch (JwtException e) {
            throw new InvalidAccessTokenException("Invalid JWT", e);
        }
    }

    @Override
    public String encode ( AccessToken accessToken ) {
        Map <String, Object> claimsMap = new HashMap <> ();
        claimsMap.put("role", accessToken.getRoles());
        if (accessToken.getUserId () != null) {
            claimsMap.put("userId", accessToken.getUserId ());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt( Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.HOURS)))
                .addClaims(claimsMap)
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(key)
                .compact();
    }
}

package nl.fontys.s7.ticketingapp.config.token.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.fontys.s7.ticketingapp.config.token.AccessToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode
@Getter
public class AccessTokenImpl implements AccessToken {
    private final String subject;
    private final Integer userId;
    private final String role;
    public AccessTokenImpl(String subject, Integer userId, String role) {
        this.subject = subject;
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String getRoles ( ) {
        return role;
    }

    @Override
    public boolean hasRole ( String roleName ) {
        return role.contains (roleName);
    }
}

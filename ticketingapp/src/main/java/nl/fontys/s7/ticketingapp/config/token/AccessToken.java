package nl.fontys.s7.ticketingapp.config.token;

import java.util.Set;

public interface AccessToken {
    String getSubject();
    Integer getUserId();
    String  getRoles();
    boolean hasRole(String roleName);
}


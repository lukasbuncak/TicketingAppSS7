package nl.fontys.s7.ticketingapp.config.token.impl;

import lombok.Value;
import nl.fontys.s7.ticketingapp.config.token.MfaToken;

@Value
public class MfaTokenImpl implements MfaToken {
    String subject;
    Integer userId;
}

package nl.fontys.s7.ticketingapp.business;

import nl.fontys.s7.ticketingapp.domain.dto.TotpSetupResponse;
import nl.fontys.s7.ticketingapp.domain.dto.TotpStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;


public interface MfaTokenService {
    TotpSetupResponse setupTotp( String AccessToken);
    void confirmTotp(String bearerToken, String code);
    TotpStatusResponse getTotpStatus( String bearerToken);
    void disableTotp(String bearerToken);
}

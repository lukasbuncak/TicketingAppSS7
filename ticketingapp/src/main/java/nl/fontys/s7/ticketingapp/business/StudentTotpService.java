package nl.fontys.s7.ticketingapp.business;

import nl.fontys.s7.ticketingapp.domain.dto.TotpSetupResponse;

public interface StudentTotpService {
    TotpSetupResponse setupTotp(int userId);
    void confirmTotp(int userId, String code);
    String verifyTotpAndIssueToken(String mfaToken, String code);
}
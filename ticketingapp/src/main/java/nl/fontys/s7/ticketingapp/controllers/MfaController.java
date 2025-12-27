package nl.fontys.s7.ticketingapp.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.MfaTokenService;
import nl.fontys.s7.ticketingapp.config.BearerTokenExtractor;
import nl.fontys.s7.ticketingapp.domain.dto.TotpConfirmRequest;
import nl.fontys.s7.ticketingapp.domain.dto.TotpSetupResponse;
import nl.fontys.s7.ticketingapp.domain.dto.TotpStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("mfa")
@RolesAllowed({"user"})
public class MfaController {
    private final MfaTokenService mfa;
    private final BearerTokenExtractor bearer;

    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.OK)
    public TotpSetupResponse setup( @RequestHeader("Authorization") String authorization) {
        String accessToken = bearer.extract(authorization);
        return mfa.setupTotp(accessToken);
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirm( @RequestHeader("Authorization") String authorization, @RequestBody @Valid TotpConfirmRequest req ) {
        String accessToken = bearer.extract(authorization);
        mfa.confirmTotp(accessToken, req.code());
    }

    // NEW: check if I have MFA
    @GetMapping("/status")
    public TotpStatusResponse status( @RequestHeader("Authorization") String authHeader) {
        String token = bearer.extract(authHeader);
        return mfa.getTotpStatus(token);
    }

    // NEW: disable MFA completely
    @PostMapping("/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@RequestHeader("Authorization") String authHeader) {
        String token = bearer.extract(authHeader);
        mfa.disableTotp(token);
    }

}



package nl.fontys.s7.ticketingapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fontys.s7.ticketingapp.business.AuthService;
import nl.fontys.s7.ticketingapp.domain.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        AuthResult result = auth.login(req);

        if (result instanceof AuthResult.Success s) {
            return ResponseEntity.ok(new LoginResponse(s.accessToken()));
        }
        if (result instanceof AuthResult.MfaRequired m) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MfaRequiredResponse (m.mfaToken()));
        }
        throw new IllegalStateException("Unknown AuthResult");
    }

    @PostMapping("/mfa/verify")
    public LoginResponse verify(@RequestHeader("Authorization") String authHeader,
                                @RequestBody @Valid TotpVerifyRequest req) {
        String mfaToken = extractBearer(authHeader);
        return auth.verifyMfa(mfaToken, req.code());
    }

    private String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException (HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }
        return header.substring("Bearer ".length()).trim();
    }
}

package nl.fontys.s7.ticketingapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fontys.s7.ticketingapp.business.AuthService;
import nl.fontys.s7.ticketingapp.domain.dto.LoginRequest;
import nl.fontys.s7.ticketingapp.domain.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login( @RequestBody @Valid LoginRequest req) {
        return auth.login(req);
    }
}

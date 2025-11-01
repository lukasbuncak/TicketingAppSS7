package nl.fontys.s7.ticketingapp.business;

import lombok.RequiredArgsConstructor;
import nl.fontys.s7.ticketingapp.domain.dto.LoginRequest;
import nl.fontys.s7.ticketingapp.domain.dto.LoginResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public interface AuthService {
    LoginResponse login( LoginRequest request);
}

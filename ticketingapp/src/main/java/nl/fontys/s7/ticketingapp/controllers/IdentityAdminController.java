package nl.fontys.s7.ticketingapp.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.IdentityAdminService;
import nl.fontys.s7.ticketingapp.domain.dto.CreateUserAccountRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UpdateUserStatusRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("admin")
public class IdentityAdminController {
    private final IdentityAdminService identityAdminService;

    @RolesAllowed({"ADMIN"})
    @PostMapping("/create/student_user")
    public ResponseEntity < UserResponse > createUserAccountAsAdmin( @RequestBody @Valid CreateUserAccountRequest request) {
        UserResponse created = identityAdminService.createUserAccount(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/users/{id}/statusUpdate")
    public ResponseEntity<UserResponse> setUserStatus(
            @RequestBody @Valid UpdateUserStatusRequest request, @PathVariable Integer id) {

        UserResponse updated = identityAdminService.setUserStatus(request);
        return ResponseEntity.ok(updated); // 200 OK with updated user
    }
}

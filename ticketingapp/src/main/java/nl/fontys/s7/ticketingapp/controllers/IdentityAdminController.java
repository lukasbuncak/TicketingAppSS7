package nl.fontys.s7.ticketingapp.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.IdentityAdminService;
import nl.fontys.s7.ticketingapp.domain.dto.AdminUserResponse;
import nl.fontys.s7.ticketingapp.domain.dto.CreateUserAccountRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UpdateUserStatusRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("admin")
public class IdentityAdminController {
    private final IdentityAdminService identityAdminService;

//    @RolesAllowed({"ADMIN"})
    @PostMapping("/create/student_user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity < AdminUserResponse > createUserAccountAsAdmin( @RequestBody @Valid CreateUserAccountRequest request) {
        AdminUserResponse created = identityAdminService.createUserAccount(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.status ())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @RolesAllowed({"ADMIN"})
    @PutMapping("/users/{id}/statusUpdate")
    public ResponseEntity< AdminUserResponse > setUserStatus(
            @RequestBody @Valid UpdateUserStatusRequest request, @PathVariable Integer id) {

        AdminUserResponse updated = identityAdminService.setUserStatus(request);
        return ResponseEntity.ok(updated); // 200 OK with updated user
    }
}

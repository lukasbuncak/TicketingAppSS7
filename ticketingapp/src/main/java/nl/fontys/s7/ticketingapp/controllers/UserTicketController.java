package nl.fontys.s7.ticketingapp.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.UserTicketService;
import nl.fontys.s7.ticketingapp.config.SecurityJWTAuthUser;
import nl.fontys.s7.ticketingapp.domain.dto.CreateTicketRequest;
import nl.fontys.s7.ticketingapp.domain.objects.TicketObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("tickets")
@RolesAllowed({"user"})
public class UserTicketController {
    private final UserTicketService userTicketsService;

    // Create ticket — POST /tickets
    @PostMapping
    public ResponseEntity < TicketObject > createTicket( @Valid @RequestBody CreateTicketRequest request) {
        Integer ownerId = SecurityJWTAuthUser.currentUserId(); // from your JWT
        TicketObject created = userTicketsService.createTicket(ownerId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // List my tickets — GET /tickets
    @GetMapping
    public ResponseEntity< Page <TicketObject> > listMyTickets(
            @PageableDefault(size = 100) Pageable pageable) {

        Integer ownerId = SecurityJWTAuthUser.currentUserId();
        Page<TicketObject> page = userTicketsService.listTicketsForOwner(ownerId, pageable);
        return ResponseEntity.ok(page);
    }

    // View my ticket — GET /tickets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TicketObject> getMyTicket(@PathVariable Integer id) {
        Integer ownerId = SecurityJWTAuthUser.currentUserId();
        TicketObject ticket = userTicketsService.getTicketForOwner(ownerId, id);
        return ResponseEntity.ok(ticket);
    }
}

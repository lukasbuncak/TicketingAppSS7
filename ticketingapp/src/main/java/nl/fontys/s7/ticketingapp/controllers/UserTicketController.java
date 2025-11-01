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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketObject> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        Integer ownerId = SecurityJWTAuthUser.currentUserId();
        TicketObject created = userTicketsService.createTicket(ownerId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId ())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<TicketObject>> listMyTickets(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Integer ownerId = SecurityJWTAuthUser.currentUserId();
        return ResponseEntity.ok(userTicketsService.listTicketsForOwner(ownerId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketObject> getMyTicket(@PathVariable Integer id) {
        Integer ownerId = SecurityJWTAuthUser.currentUserId();
        return ResponseEntity.ok(userTicketsService.getTicketForOwner(ownerId, id));
    }
}

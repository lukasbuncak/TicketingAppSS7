package nl.fontys.s7.ticketingapp.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.domain.dto.CreateTicketRequest;
import nl.fontys.s7.ticketingapp.domain.enumerations.TicketStatus;
import nl.fontys.s7.ticketingapp.domain.objects.TicketObject;
import nl.fontys.s7.ticketingapp.persistance.entities.TicketEntity;
import nl.fontys.s7.ticketingapp.persistance.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import nl.fontys.s7.ticketingapp.business.UserTicketService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
public class UserTicketServiceImpl implements UserTicketService {
    private final TicketRepository tickets;

    @Override
    public TicketObject createTicket ( Integer ownerId, CreateTicketRequest req ) {
        TicketEntity e = new TicketEntity();
        e.setTitle(req.title().trim());
        e.setDescription(req.description().trim());
        e.setOwnerId(ownerId);
        // if you kept String status:
        // e.setStatus("OPEN");
        // if you switched to enum:
        e.setStatus( TicketStatus.OPEN.toString ());

        TicketEntity saved = tickets.save(e);
        return toObject(saved);
    }

    private TicketObject toObject(TicketEntity e) {
        return new TicketObject(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStatus(),  // or e.getStatus() if String
                e.getOwnerId(),
                null,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    @Override
    public Page<TicketObject> listTicketsForOwner(Integer ownerId, Pageable pageable) {
        return tickets.findByOwnerId(ownerId, pageable).map(this::toObject);
    }

    @Override
    public TicketObject getTicketForOwner(Integer ownerId, Integer ticketId) {
        TicketEntity e = tickets.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException (NOT_FOUND, "Ticket not found"));
        if (!e.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(FORBIDDEN, "Ticket not found");
        }
        return toObject(e);
    }
}

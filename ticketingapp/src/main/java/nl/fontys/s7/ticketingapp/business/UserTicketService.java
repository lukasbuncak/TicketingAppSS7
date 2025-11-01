package nl.fontys.s7.ticketingapp.business;

import nl.fontys.s7.ticketingapp.domain.dto.CreateTicketRequest;
import nl.fontys.s7.ticketingapp.domain.objects.TicketObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserTicketService {
    TicketObject createTicket(Integer ownerId, CreateTicketRequest request);
    Page<TicketObject> listTicketsForOwner(Integer ownerId, Pageable pageable);
    TicketObject getTicketForOwner(Integer ownerId, Integer ticketId);
}

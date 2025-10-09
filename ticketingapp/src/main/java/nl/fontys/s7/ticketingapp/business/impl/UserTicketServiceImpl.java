package nl.fontys.s7.ticketingapp.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.domain.dto.CreateTicketRequest;
import nl.fontys.s7.ticketingapp.domain.objects.TicketObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import nl.fontys.s7.ticketingapp.business.UserTicketService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserTicketServiceImpl implements UserTicketService {
    @Override
    public TicketObject createTicket ( Integer ownerId, CreateTicketRequest request ) {
        return null;
    }

    @Override
    public Page < TicketObject > listTicketsForOwner ( Integer ownerId, Pageable pageable ) {
        return null;
    }

    @Override
    public TicketObject getTicketForOwner ( Integer ownerId, Integer ticketId ) {
        return null;
    }
}

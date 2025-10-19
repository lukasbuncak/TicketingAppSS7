package nl.fontys.s7.ticketingapp.persistance.repository;

import nl.fontys.s7.ticketingapp.persistance.entities.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TicketRepository extends JpaRepository< TicketEntity, Long> {
    Page <TicketEntity> findByOwnerId( Integer ownerId, Pageable pageable);
    Page<TicketEntity> findByAssigneeId(Integer assigneeId, Pageable pageable);
    Page<TicketEntity> findByStatusIn( List <String> status, Pageable pageable);
    Page<TicketEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

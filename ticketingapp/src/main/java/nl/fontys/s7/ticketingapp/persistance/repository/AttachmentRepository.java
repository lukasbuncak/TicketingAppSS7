package nl.fontys.s7.ticketingapp.persistance.repository;


import nl.fontys.s7.ticketingapp.persistance.entities.AttachmentMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<AttachmentMetaEntity, Long> {
    List< AttachmentMetaEntity > findByTicketId( Long ticketId);
}


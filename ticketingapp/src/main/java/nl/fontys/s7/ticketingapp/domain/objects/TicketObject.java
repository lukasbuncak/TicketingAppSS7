package nl.fontys.s7.ticketingapp.domain.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import nl.fontys.s7.ticketingapp.domain.enumerations.TicketStatus;

import java.time.Instant;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketObject {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private Integer ownerId;
    private Integer assigneeId;   // can be null
    private Instant createdAt;
    private Instant updatedAt;


}

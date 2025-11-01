package nl.fontys.s7.ticketingapp.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String description
        // priority/category can be added later
) {}

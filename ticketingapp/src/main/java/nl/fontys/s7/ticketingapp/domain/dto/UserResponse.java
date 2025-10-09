package nl.fontys.s7.ticketingapp.domain.dto;

public record UserResponse() {
    static Integer id;
    static String email;
    static String personalEmail;
    static String firstName;
    static String lastName;
    static String role;

    public Integer id ( ) {
        return id;
    }
}

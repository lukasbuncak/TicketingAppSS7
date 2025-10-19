package nl.fontys.s7.ticketingapp.business.exception;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String message) {
        super(message);
    }
    public InvalidAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

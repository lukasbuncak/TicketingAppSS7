package nl.fontys.s7.ticketingapp.business.exception;

public class InvalidMfaTokenException extends RuntimeException {
    public InvalidMfaTokenException(String message, Throwable cause) { super(message, cause); }
    public InvalidMfaTokenException(String message) { super(message); }
}
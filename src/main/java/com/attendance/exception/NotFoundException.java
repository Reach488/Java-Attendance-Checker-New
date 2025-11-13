package com.attendance.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 */
public class NotFoundException extends RuntimeException {
    
    /**
     * Constructor with message.
     * @param message the error message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.student;

/**
 * This class is defined to capture all runtime exception which might happen at repository level.
 */
public class RepositoryRuntimeException extends RuntimeException{
    public RepositoryRuntimeException() {
        super("Not able to find the collection");
    }

    // Constructor with custom message
    public RepositoryRuntimeException(String message) {
        super(message);
    }

    public RepositoryRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}

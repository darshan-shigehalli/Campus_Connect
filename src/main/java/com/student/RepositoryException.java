package com.student;

/**
 * This class is defined to capture all kind of exception which might happen at repository level.
 */
public class RepositoryException extends Exception{
        public RepositoryException(String message) {
            super(message);
        }

        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }


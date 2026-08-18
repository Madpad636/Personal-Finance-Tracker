package com.financeapp.exception;

/** Thrown when a user tries to access/modify a resource that isn't theirs. */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

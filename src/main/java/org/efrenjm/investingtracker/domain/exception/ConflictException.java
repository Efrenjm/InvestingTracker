package org.efrenjm.investingtracker.domain.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super("Conflict: " + message);
    }
}

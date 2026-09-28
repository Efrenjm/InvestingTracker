package org.efrenjm.investingtracker.interfaces.rest.exception.client;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

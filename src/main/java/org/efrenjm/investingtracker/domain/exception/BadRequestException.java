package org.efrenjm.investingtracker.domain.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super("Bad request: " + message);
    }
}

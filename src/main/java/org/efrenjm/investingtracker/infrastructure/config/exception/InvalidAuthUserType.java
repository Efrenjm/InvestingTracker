package org.efrenjm.investingtracker.infrastructure.config.exception;

public class InvalidAuthUserType extends RuntimeException {
    public InvalidAuthUserType(String message) {
        super(message);
    }
}

package org.efrenjm.investingtracker.interfaces.rest.exception.client;

import org.efrenjm.investingtracker.interfaces.rest.exception.base.ClientErrorException;

public class BadRequestException extends ClientErrorException {
    public BadRequestException(String message) {
        super(400, "BAD_REQUEST", message);
    }

    public BadRequestException(String message, Object details) {
        super(400, "BAD_REQUEST", message, details);
    }
}

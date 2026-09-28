package org.efrenjm.investingtracker.interfaces.rest.exception.client;

import org.efrenjm.investingtracker.interfaces.rest.exception.base.ClientErrorException;

public class UnauthorizedException extends ClientErrorException {
    public UnauthorizedException(String message) {
        super(401, "UNAUTHORIZED", message);
    }

    public UnauthorizedException() {
        super(401, "UNAUTHORIZED", "Authentication required");
    }
}

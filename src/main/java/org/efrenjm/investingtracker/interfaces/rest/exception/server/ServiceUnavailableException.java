package org.efrenjm.investingtracker.interfaces.rest.exception.server;

import org.efrenjm.investingtracker.interfaces.rest.exception.base.ServerErrorException;

public class ServiceUnavailableException extends ServerErrorException {
    public ServiceUnavailableException(String message, Throwable cause) {
        super(503, "SERVICE_UNAVAILABLE", message, cause);
    }

    public ServiceUnavailableException(String service) {
        super(
                503,
                "SERVICE_UNAVAILABLE",
                String.format("Service %s is currently unavailable", service));
    }
}

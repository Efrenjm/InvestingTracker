package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException(String email) {
        super("Invalid email address: " + email);
    }
}

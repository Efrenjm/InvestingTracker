package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException(String email) {
        super("Invalid email address: " + email);
    }
}


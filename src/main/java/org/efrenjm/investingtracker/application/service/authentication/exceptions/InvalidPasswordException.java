package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException() {
        super("Invalid password. It should be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
    }
}

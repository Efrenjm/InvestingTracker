package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class AccountAlreadyVerifiedException extends BadRequestException {
    public AccountAlreadyVerifiedException() {
        super("Account already verified. Please log in.");
    }
}

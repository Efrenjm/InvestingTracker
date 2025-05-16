package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidPhoneNumberException extends BadRequestException {
    public InvalidPhoneNumberException(String phoneNumber) {
        super("Invalid phone number: " + phoneNumber);
    }
}

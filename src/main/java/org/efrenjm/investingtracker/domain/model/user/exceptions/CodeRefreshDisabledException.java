package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class CodeRefreshDisabledException extends BadRequestException {
    public CodeRefreshDisabledException() {
        super("Code refresh is disabled. Please try again later.");
    }
}

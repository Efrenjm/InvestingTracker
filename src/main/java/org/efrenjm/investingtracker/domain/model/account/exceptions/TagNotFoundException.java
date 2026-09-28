package org.efrenjm.investingtracker.domain.model.account.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class TagNotFoundException extends BadRequestException {
    public TagNotFoundException(String tag) {
        super("Tag '" + tag + "' doesn't exist in the account.");
    }
}

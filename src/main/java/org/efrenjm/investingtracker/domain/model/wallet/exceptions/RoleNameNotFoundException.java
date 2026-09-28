package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class RoleNameNotFoundException extends BadRequestException {
    public RoleNameNotFoundException(String roleName) {
        super("Role " + roleName + " not found");
    }
}

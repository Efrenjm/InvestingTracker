package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class MemberNotInWalletException extends BadRequestException {
    public MemberNotInWalletException(String userId) {
        super("User with ID " + userId + " is not assigned to any role in this wallet.");
    }
}

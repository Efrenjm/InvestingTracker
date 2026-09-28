package org.efrenjm.investingtracker.application.service.wallet_management.exceptions;

import org.efrenjm.investingtracker.domain.exception.NotAuthorizedException;

public class UnauthorizedWalletAccessException extends NotAuthorizedException {
    public UnauthorizedWalletAccessException(String walletId) {
        super("You are not authorized to access wallet with id: " + walletId);
    }
}

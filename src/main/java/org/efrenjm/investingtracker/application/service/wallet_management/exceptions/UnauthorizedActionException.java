package org.efrenjm.investingtracker.application.service.wallet_management.exceptions;

import org.efrenjm.investingtracker.domain.exception.NotAuthorizedException;

public class UnauthorizedActionException extends NotAuthorizedException {
    public UnauthorizedActionException(String action, String walletId) {
        super(
                "You are not authorized to perform action '"
                        + action
                        + "' on wallet with id: "
                        + walletId);
    }
}

package org.efrenjm.investingtracker.domain.model.account.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class WalletNotSharingAccountException extends BadRequestException {
    public WalletNotSharingAccountException(String walletId) {
        super("Wallet " + walletId + " doesn't have view access to this account.");
    }
}

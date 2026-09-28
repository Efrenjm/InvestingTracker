package org.efrenjm.investingtracker.infrastructure.persistence.entity.account.exception;

public class NonSupportedAccountTypeException extends RuntimeException {
    public NonSupportedAccountTypeException(String accountType) {
        super("Non supported account of type: " + accountType);
    }
}

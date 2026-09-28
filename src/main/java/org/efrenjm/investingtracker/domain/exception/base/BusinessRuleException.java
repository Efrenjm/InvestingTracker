package org.efrenjm.investingtracker.domain.exception.base;

public abstract class BusinessRuleException extends DomainException {
    protected BusinessRuleException(String errorCode, String message) {
        super(errorCode, message, null);
    }

    protected BusinessRuleException(String errorCode, String message, Object details) {
        super(errorCode, message, details);
    }
}

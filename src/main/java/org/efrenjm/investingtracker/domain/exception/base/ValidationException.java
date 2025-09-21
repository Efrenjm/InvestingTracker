package org.efrenjm.investingtracker.domain.exception.base;

public abstract class ValidationException extends DomainException
{
	protected ValidationException(String errorCode, String message, Object details)
	{
		super(errorCode, message, details);
	}
}
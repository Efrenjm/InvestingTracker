package org.efrenjm.investingtracker.domain.exception.base;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException
{
	private final String errorCode;
	private final Object details;

	protected DomainException(String errorCode, String message, Object details)
	{
		super(message);
		this.errorCode = errorCode;
		this.details = details;
	}

	protected DomainException(String errorCode, String message, Throwable cause)
	{
		super(message, cause);
		this.errorCode = errorCode;
		this.details = null;
	}
}
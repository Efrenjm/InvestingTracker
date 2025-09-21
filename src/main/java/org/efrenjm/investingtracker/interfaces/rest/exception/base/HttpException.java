package org.efrenjm.investingtracker.interfaces.rest.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class HttpException extends RuntimeException
{
	private final int statusCode;
	private final String errorCode;
	private final Object details;

	protected HttpException(int statusCode, String errorCode, String message, Object details)
	{
		super(message);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.details = details;
	}

	protected HttpException(int statusCode, String errorCode, String message, Throwable cause)
	{
		super(message, cause);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.details = null;
	}
}


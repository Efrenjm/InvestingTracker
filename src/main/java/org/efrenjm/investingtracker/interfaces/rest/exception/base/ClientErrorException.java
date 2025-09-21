package org.efrenjm.investingtracker.interfaces.rest.exception.base;

public abstract class ClientErrorException extends HttpException
{
	protected ClientErrorException(int statusCode, String errorCode, String message, Object details)
	{
		super(statusCode, errorCode, message, details);
	}

	protected ClientErrorException(int statusCode, String errorCode, String message)
	{
		super(statusCode, errorCode, message, null);
	}
}
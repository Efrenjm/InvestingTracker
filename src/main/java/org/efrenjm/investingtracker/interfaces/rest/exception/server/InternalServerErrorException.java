package org.efrenjm.investingtracker.interfaces.rest.exception.server;

import org.efrenjm.investingtracker.interfaces.rest.exception.base.ServerErrorException;

public class InternalServerErrorException extends ServerErrorException
{
	public InternalServerErrorException(String message, Throwable cause)
	{
		super(500, "INTERNAL_SERVER_ERROR", message, cause);
	}

	public InternalServerErrorException(String message)
	{
		super(500, "INTERNAL_SERVER_ERROR", message);
	}
}

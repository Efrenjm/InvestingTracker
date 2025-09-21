package org.efrenjm.investingtracker.interfaces.rest.exception.client;

import org.efrenjm.investingtracker.interfaces.rest.exception.base.ClientErrorException;

public class NotFoundException extends ClientErrorException
{
	public NotFoundException(String message)
	{
		super(404, "NOT_FOUND", message);
	}

	public NotFoundException(String resource, String identifier)
	{
		super(404, "RESOURCE_NOT_FOUND",
				String.format("%s with identifier '%s' not found", resource, identifier));
	}
}

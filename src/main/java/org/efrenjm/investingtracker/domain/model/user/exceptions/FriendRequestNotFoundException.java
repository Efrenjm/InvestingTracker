package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class FriendRequestNotFoundException extends BadRequestException
{
	public FriendRequestNotFoundException(String userId, boolean outgoing)
	{
		super("There is no pending friend request " + (outgoing ? "to" : "from") + " user with id " + userId);
	}
}


package org.efrenjm.investingtracker.domain.model.user.exceptions;

public class PendingFriendRequestException extends RuntimeException
{
	public PendingFriendRequestException(boolean isWaitingInvitee, String friendId)
	{
		super("There is already a pending friend request" + (isWaitingInvitee ? "to" : "from") + "user with id " + friendId);
	}
}

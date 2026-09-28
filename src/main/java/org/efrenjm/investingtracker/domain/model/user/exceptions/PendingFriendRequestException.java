package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class PendingFriendRequestException extends ConflictException {
    public PendingFriendRequestException(boolean isWaitingInvitee, String friendId) {
        super(
                "There is already a pending friend request "
                        + (isWaitingInvitee ? "to" : "from")
                        + " user with id "
                        + friendId);
    }
}

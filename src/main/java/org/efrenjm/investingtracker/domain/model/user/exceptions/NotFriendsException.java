package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class NotFriendsException extends BadRequestException {
    public NotFriendsException(String userId, String friendId) {
        super("User with id " + friendId + " is not a friend of user with id " + userId);
    }
}

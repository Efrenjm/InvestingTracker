package org.efrenjm.investingtracker.application.service.user_service.exceptions;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
	public UserNotFoundException(ObjectId userId) {
		super(String.format("User with id  %s not found.", userId));
	}

	public UserNotFoundException(String username) {
		super(String.format("User with username %s not found.", username));
	}
}

package org.efrenjm.investingtracker.exception.user_management;

import org.efrenjm.investingtracker.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
	public UserNotFoundException(String userId) {
		super(String.format("User with id  %s not found.", userId));
	}
}

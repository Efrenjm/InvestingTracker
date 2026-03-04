package org.efrenjm.investingtracker.application.service.user_service.exceptions;

import org.efrenjm.investingtracker.domain.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
	public UserNotFoundException(String identifier) {
		super(String.format("User with identifier '%s' not found.", identifier));
	}
}

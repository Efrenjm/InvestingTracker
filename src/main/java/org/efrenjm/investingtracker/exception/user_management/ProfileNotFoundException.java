package org.efrenjm.investingtracker.exception.user_management;

import org.efrenjm.investingtracker.exception.ResourceNotFoundException;

public class ProfileNotFoundException extends ResourceNotFoundException {
	public ProfileNotFoundException(String userId) {
		super(String.format("Profile with id  %s not found.", userId));
	}
}

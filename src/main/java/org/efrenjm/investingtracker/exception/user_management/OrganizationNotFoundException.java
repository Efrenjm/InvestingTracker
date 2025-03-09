package org.efrenjm.investingtracker.exception.user_management;

import org.efrenjm.investingtracker.exception.ResourceNotFoundException;

public class OrganizationNotFoundException extends ResourceNotFoundException {
	public OrganizationNotFoundException(String organizationId) {
		super(String.format("Organization with id  %s not found.", organizationId));
	}
}

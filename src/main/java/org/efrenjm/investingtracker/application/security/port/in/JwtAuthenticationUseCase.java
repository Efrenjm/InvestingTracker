package org.efrenjm.investingtracker.application.security.port.in;

import org.efrenjm.investingtracker.domain.model.utils.SystemRole;

import java.util.Set;

/**
 * JWT capability required by the inbound WebFlux authentication filter.
 */
public interface JwtAuthenticationUseCase
{
	boolean isValidToken(String token);

	String extractSessionId(String token);

	String extractUserId(String token);

	Set<SystemRole> extractRoles(String token);
}

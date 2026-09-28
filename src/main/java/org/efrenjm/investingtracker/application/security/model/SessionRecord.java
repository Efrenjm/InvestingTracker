package org.efrenjm.investingtracker.application.security.model;

import java.time.Instant;
import java.util.Set;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;

/**
 * Framework-neutral representation of an authenticated session.
 *
 * @param sessionId unique identifier assigned to the session
 * @param userId identifier of the authenticated user
 * @param roles roles granted to the user for this session
 * @param issuedAt time at which the session was issued
 * @param expiration time at which the session expires
 */
public record SessionRecord(
        String sessionId,
        String userId,
        Set<SystemRole> roles,
        Instant issuedAt,
        Instant expiration) {
    public SessionRecord {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }
}

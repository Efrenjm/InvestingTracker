package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import org.efrenjm.investingtracker.domain.model.user.UserPreferences;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public interface SelfProfileProjection extends PublicProfileProjection {
	Set<GrantedAuthority> getRoles();
	UserPreferences getPreferences();
}

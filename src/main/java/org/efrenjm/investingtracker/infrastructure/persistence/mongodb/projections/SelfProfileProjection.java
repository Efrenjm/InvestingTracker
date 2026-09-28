package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import java.util.Set;
import org.efrenjm.investingtracker.domain.model.user.UserPreferences;
import org.springframework.security.core.GrantedAuthority;

public interface SelfProfileProjection extends PublicProfileProjection {
    Set<GrantedAuthority> getRoles();

    UserPreferences getPreferences();
}

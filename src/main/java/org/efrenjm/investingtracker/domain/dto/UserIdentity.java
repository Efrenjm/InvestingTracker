package org.efrenjm.investingtracker.domain.dto;

import java.util.Set;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;

public record UserIdentity(String id, Set<SystemRole> roles) {
    public static UserIdentity from(User user) {
        return new UserIdentity(user.getId(), user.getRoles());
    }
}

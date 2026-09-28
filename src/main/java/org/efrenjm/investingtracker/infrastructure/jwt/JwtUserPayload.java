package org.efrenjm.investingtracker.infrastructure.jwt;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;

@Getter
@Setter
@AllArgsConstructor
public class JwtUserPayload {
    String id;
    Set<SystemRole> roles;

    //	public static JwtUserPayload fromUserIdentity(UserIdentity user) {
    //		return new JwtUserPayload(
    //				user.id(),
    //				user.roles()
    //		);
    //	}
    //
    //	public static JwtUserPayload fromUser(User user) {
    //		return new JwtUserPayload(
    //				user.getId(),
    //				user.getRoles()
    //		);
    //	}
}

package org.efrenjm.investingtracker.model.Organization;

import lombok.*;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.efrenjm.investingtracker.model.Role.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRole {
    private Profile user;

    private Role role;
}

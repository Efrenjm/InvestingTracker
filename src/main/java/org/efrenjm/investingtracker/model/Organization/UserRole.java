package org.efrenjm.investingtracker.model.Organization;

import lombok.*;
import org.efrenjm.investingtracker.model.Profile.PublicProfile;
import org.efrenjm.investingtracker.model.Role.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRole {
    private PublicProfile user;
    private Role role;
}

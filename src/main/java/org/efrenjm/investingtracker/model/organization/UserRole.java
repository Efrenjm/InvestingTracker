package org.efrenjm.investingtracker.model.organization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.model.role.Role;
import org.springframework.data.mongodb.core.mapping.DBRef;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRole {
    @DBRef
    @JsonBackReference
    private Profile user;

    private String role;
}

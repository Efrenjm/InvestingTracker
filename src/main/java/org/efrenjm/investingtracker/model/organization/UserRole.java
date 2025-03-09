package org.efrenjm.investingtracker.model.organization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.model.role.Role;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRole {
    @DocumentReference
    @JsonBackReference
    private Profile user;

    private String role;
}

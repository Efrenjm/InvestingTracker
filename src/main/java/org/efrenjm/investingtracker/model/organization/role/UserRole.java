package org.efrenjm.investingtracker.model.organization.role;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.user.User;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRole {
    private ObjectId user;

    private String roleName;
}

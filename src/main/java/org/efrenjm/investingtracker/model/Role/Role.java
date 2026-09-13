package org.efrenjm.investingtracker.model.Role;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "roles")
public class Role {
    @Field("_id")
    private String id;

    private String name;

    private String description;

    private Permissions permissions;
}

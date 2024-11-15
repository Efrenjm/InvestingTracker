package org.efrenjm.investingtracker.model.Role;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "roles")
public class Role {
    private String id;
    private String name;
    private String description;
    private Permissions permissions;
}

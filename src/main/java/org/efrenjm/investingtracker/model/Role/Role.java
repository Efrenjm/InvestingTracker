package org.efrenjm.investingtracker.model.Role;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
    private String id;
    private String name;
    private String description;
    private Permissions permissions;
}

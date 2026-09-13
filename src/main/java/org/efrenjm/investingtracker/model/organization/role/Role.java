package org.efrenjm.investingtracker.model.organization.role;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Role {
    private String name;

    private String description;

    private Permissions permissions;
}

package org.efrenjm.investingtracker.model.Role;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Permissions {
    private Boolean createAccounts;

    private Boolean createTransactions;

    private Boolean createRules;

    private Boolean addUsers;

    private Boolean editOrganization;

    private Boolean editAccounts;

    private Boolean editTransactions;

    private Boolean editRules;

    private Boolean editUserRoles;

    private Boolean viewAccounts;

    private Boolean viewTransactions;

    private Boolean viewRules;

    private Boolean viewUserRoles;

    private Boolean deleteOrganization;

    private Boolean deleteAccounts;

    private Boolean deleteTransactions;

    private Boolean deleteRules;

    private Boolean removeUsers;
}

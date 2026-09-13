package org.efrenjm.investingtracker.model.organization.role;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Permissions {
    private Boolean createAccounts;

    private Boolean createTransactions;

    private Boolean createRules;

    private Boolean addMembers;

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

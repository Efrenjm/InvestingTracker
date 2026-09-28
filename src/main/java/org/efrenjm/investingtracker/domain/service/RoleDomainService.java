package org.efrenjm.investingtracker.domain.service;

import java.util.HashMap;
import java.util.Map;
import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleDomainService {
    public Map<String, Role> createDefaultRoles() {
        HashMap<String, Role> roles = new HashMap<>();
        roles.put("Owner", owner());
        roles.put("Manager", manager());
        roles.put("Viewer", viewer());
        return roles;
    }

    private Role owner() {
        return Role.builder()
                .description("Role with all permissions")
                .permissions(ownerPermissions())
                .build();
    }

    private Role manager() {
        return Role.builder()
                .description(
                        "Role for general use, all permissions except for removing accounts or editing wallet")
                .permissions(managerPermissions())
                .build();
    }

    private Role viewer() {
        return Role.builder()
                .description("Role for viewing only, with no additional permissions")
                .permissions(viewerPermissions())
                .build();
    }

    private Role.Permissions ownerPermissions() {
        return Role.Permissions.builder()
                .accounts(allCrudPermissions())
                .transactions(allCrudPermissions())
                .rules(allCrudPermissions())
                .members(allCrudPermissions())
                .wallet(allCrudPermissions())
                .build();
    }

    private Role.Permissions managerPermissions() {
        return Role.Permissions.builder()
                .accounts(allCrudExceptRemovePermissions())
                .transactions(allCrudPermissions())
                .rules(allCrudPermissions())
                .members(allCrudPermissions())
                .wallet(viewOnlyPermissions())
                .build();
    }

    private Role.Permissions viewerPermissions() {
        return Role.Permissions.builder()
                .accounts(viewOnlyPermissions())
                .transactions(viewOnlyPermissions())
                .rules(viewOnlyPermissions())
                .members(viewOnlyPermissions())
                .wallet(viewOnlyPermissions())
                .build();
    }

    private Role.Permissions.CRUDPermissions allCrudPermissions() {
        return Role.Permissions.CRUDPermissions.builder()
                .add(true)
                .edit(true)
                .view(true)
                .remove(true)
                .build();
    }

    private Role.Permissions.CRUDPermissions allCrudExceptRemovePermissions() {
        return Role.Permissions.CRUDPermissions.builder()
                .add(true)
                .edit(true)
                .view(true)
                .remove(false)
                .build();
    }

    private Role.Permissions.CRUDPermissions viewOnlyPermissions() {
        return Role.Permissions.CRUDPermissions.builder()
                .add(false)
                .edit(false)
                .view(true)
                .remove(false)
                .build();
    }
}

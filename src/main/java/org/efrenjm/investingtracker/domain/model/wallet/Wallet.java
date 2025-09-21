package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.RoleNameNotFoundException;

import java.util.*;


@Getter
@Setter
@SuperBuilder
@ToString
public class Wallet extends AuditableModel
{
    private String name;

    private String description;

    @Builder.Default
    private Map<String, Role> roles = new HashMap<>();

    @Builder.Default
    private Set<String> accounts = new HashSet<>();

    private WalletConfig configuration;

    public Optional<Map<String, Role>> getRoles()
    {
        return Optional.ofNullable(roles);
    }

    public Optional<Set<String>> getAccounts()
    {
        return Optional.ofNullable(accounts);
    }

    public Optional<WalletConfig> getConfiguration()
    {
        return Optional.ofNullable(configuration);
    }

//    public void setName(String name, String updaterUserId)
//    {
//        this.name = name;
//        touchModel(updaterUserId);
//    }
//
//    public void setDescription(String description, String updaterUserId)
//    {
//        this.description = description;
//        touchModel(updaterUserId);
//    }

    public void addMemberToRole(String roleName, String userId)
    {
        if (!roles.containsKey(roleName))
        {
            throw new RoleNameNotFoundException(roleName);
        }
        Optional<String> currentRole = findRoleOfUser(userId);
	    currentRole.ifPresent(s -> roles.computeIfPresent(s, (key, role) -> {
		    role.removeMember(userId);
		    return role;
	    }));

        roles.computeIfPresent(roleName, (key, role) -> {
            role.addMember(userId);
            return role;
        });
    }

    public void removeMember(String userId)
    {
        Optional<String> roleName = findRoleOfUser(userId);
        if (roleName.isEmpty())
        {
            throw new IllegalArgumentException("User with ID " + userId + " is not assigned to any role in this wallet.");
        }
        roles.computeIfPresent(roleName.get(), (key, role) -> {
            role.removeMember(userId);
            return role;
        });
    }

    public Optional<String> findRoleOfUser(String userId)
    {
        for (Map.Entry<String, Role> entry : roles.entrySet()) {
            if (entry.getValue().getMembers().contains(userId)) {
                return Optional.of(entry.getKey()); // Return the role name
            }
        }
        return Optional.empty(); // User not found in any role
    }

    public void addRole(String roleName, Role role)
    {
        if (roles.containsKey(roleName))
        {
            throw new IllegalArgumentException(roleName + "role already exist in this wallet");
        }
        roles.put(roleName, role);
    }

    public void removeRole(String roleName)
    {
        if (!roles.containsKey(roleName))
        {
            throw new RoleNameNotFoundException(roleName);
        }
        roles.remove(roleName);
    }


    public void linkAccount(BaseAccount newAccount)
    {
        if (accounts == null)
        {
            accounts = new HashSet<>();
        }
        String accountId = newAccount.getId();
        if (accounts.contains(accountId))
        {
            throw new IllegalArgumentException("Account with ID " + accountId + " already exists in the wallet.");
        }

        accounts.add(accountId);
        newAccount.setWalletId(this.id);
    }

    public void unlinkAccount(BaseAccount accountToUnlink)
    {
        if (accounts == null)
        {
            accounts = new HashSet<>();
        }
        String accountId = accountToUnlink.getId();
        if (!accounts.contains(accountId)) {
            throw new IllegalArgumentException("Account with ID " + accountId + " doesn't belong to the wallet.");
        }

        accounts.remove(accountId);
        accountToUnlink.setWalletId(null);
    }
}

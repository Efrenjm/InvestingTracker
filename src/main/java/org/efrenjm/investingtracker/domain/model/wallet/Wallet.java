package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.AccountLinkedToAnotherWalletException;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.AccountNotLinkedToWallet;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.MemberNotInWalletException;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.RoleAlreadyExistsException;
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
    private Visibility visibility = Visibility.PRIVATE;

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
            throw new MemberNotInWalletException(userId);
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
            throw new RoleAlreadyExistsException(roleName);
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
        ensureAccountsSet();
        String accountId = newAccount.getId();
        if (newAccount.getWalletId() != null && !newAccount.getWalletId().equals(this.id))
        {
            throw new AccountLinkedToAnotherWalletException(accountId);
        }
        accounts.add(accountId);
        newAccount.setWalletId(this.id);
    }

    public void unlinkAccount(BaseAccount accountToUnlink)
    {
        ensureAccountsSet();
        String accountId = accountToUnlink.getId();
        if (!accounts.contains(accountId)) {
            throw new AccountNotLinkedToWallet(accountId, this.id);
        }

        accounts.remove(accountId);
        accountToUnlink.setWalletId(null);
    }

    private void ensureAccountsSet()
    {
        if (accounts == null)
        {
            accounts = new HashSet<>();
        }
    }
}

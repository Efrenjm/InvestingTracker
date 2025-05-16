package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.RoleNameNotFoundException;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Wallet {
    private String id;
    private String name;
    private String description;
    private List<List<Role>> roles;
    private List<String> accounts;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    private WalletConfig configuration;

    public void addMemberToRole(String roleName, String userId) {
        for (List<Role> roleStage : roles) {
            for (Role role : roleStage) {
                if (role.getName().equals(roleName)) {
                    role.addMember(userId);
                    return;
                }
            }
        }
        throw new RoleNameNotFoundException(roleName);
    }

    public static Wallet defaultWallet(String walletId, String userId, String accountId) {
        Date now = new Date();
        WalletConfig defaultConfig = WalletConfig.defaultConfig();
        Wallet defaultWallet = Wallet.builder()
                .id(walletId)
                .name("Personal")
                .description("Personal wallet")
                .accounts(List.of(accountId))
                .roles(Role.defaultRoles())
                .createdBy(userId)
                .createdAt(now)
                .updatedAt(now)
                .configuration(defaultConfig)
                .build();
        defaultWallet.addMemberToRole("Owner", userId);
        return defaultWallet;
    }
}

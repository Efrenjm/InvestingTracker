package org.efrenjm.investingtracker.domain.model.account;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Account {
    private String id;
    private String name;
    private String description;
    private String walletId;
    private AccountType type;
    @Builder.Default
    private List<String> sharingWallets = new ArrayList<>();
    @Builder.Default
    private Double available = 0.0;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private Date createdAt = new Date();
    @Builder.Default
    private Date updatedAt = new Date();
    @Builder.Default
    private AccountConfig accountConfig = AccountConfig.defaultConfig();
    @Builder.Default
    private List<String> rules = new ArrayList<>();

    /* Debit */
    private Double goal;

    /* Assets */
    private String asset;
    private Double currentPrice;
    private Double averageCost;

    /* Credit */
    private Double currentDebt;
    private Double creditLimit;

    public static Account defaultAccount(String accountId, String walletId) {
        return  Account.builder()
                .id(accountId)
                .name("Personal")
                .description("Personal account")
                .walletId(walletId)
                .type(AccountType.DEBIT)
                .goal(0.0)
                .build();
    }
}

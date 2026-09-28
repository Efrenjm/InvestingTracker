package org.efrenjm.investingtracker.domain.service;

import java.util.Date;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.model.wallet.WalletConfig;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletDomainService {
    private final IdGeneratorPort idGenerator;
    private final RoleDomainService roleDomainService;
    private final TransactionCategoryDomainService transactionCategoryDomainService;

    public Wallet createWallet(
            String creatorUserId, String name, String description, Visibility visibility) {
        Date now = new Date();
        Wallet defaultWallet =
                Wallet.builder()
                        .id(idGenerator.generateId())
                        .name(name)
                        .description(description)
                        .visibility(visibility)
                        .roles(roleDomainService.createDefaultRoles())
                        .configuration(createDefaultConfig())
                        .createdBy(creatorUserId)
                        .createdAt(now)
                        .updatedBy(creatorUserId)
                        .updatedAt(now)
                        .build();
        defaultWallet.addMemberToRole("Owner", creatorUserId);
        return defaultWallet;
    }

    public Wallet createWallet(String creatorUserId, String name, String description) {
        return createWallet(creatorUserId, name, description, Visibility.PRIVATE);
    }

    private WalletConfig createDefaultConfig() {
        return WalletConfig.builder()
                .rules(new HashSet<>())
                .transactionCategories(
                        transactionCategoryDomainService.createDefaultTransactionCategories())
                .build();
    }
}

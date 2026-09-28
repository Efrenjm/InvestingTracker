package org.efrenjm.investingtracker.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.efrenjm.investingtracker.domain.model.account.AccountType;
import org.efrenjm.investingtracker.domain.model.account.AssetAccount;
import org.efrenjm.investingtracker.domain.model.account.CreditAccount;
import org.efrenjm.investingtracker.domain.model.account.DebitAccount;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountDomainServiceTest {

    @Mock private IdGeneratorPort idGenerator;

    @InjectMocks private AccountDomainService accountDomainService;

    @Test
    void createDebitAccountShouldCreateDebitAccountWithGeneratedIdAndDefaults() {
        when(idGenerator.generateId()).thenReturn("acc-1");

        DebitAccount account = accountDomainService.createDebitAccount("Personal", "Main account");

        assertEquals("acc-1", account.getId());
        assertEquals("Personal", account.getName());
        assertEquals("Main account", account.getDescription());
        assertEquals(AccountType.DEBIT, account.getType());
        assertEquals(0.0, account.getAvailable());
        assertEquals(0.0, account.getGoal());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    void createCreditAccountShouldCreateCreditAccountWithDebtAndLimit() {
        when(idGenerator.generateId()).thenReturn("acc-2");

        CreditAccount account =
                accountDomainService.createCreditAccount("Credit", "Credit card", 1200.0, 5000.0);

        assertEquals("acc-2", account.getId());
        assertEquals(AccountType.CREDIT, account.getType());
        assertEquals(0.0, account.getAvailable());
        assertEquals(1200.0, account.getCurrentDebt());
        assertEquals(5000.0, account.getCreditLimit());
    }

    @Test
    void createAssetAccountShouldCreateAssetAccountWithPricingAndGoal() {
        when(idGenerator.generateId()).thenReturn("acc-3");

        AssetAccount account =
                accountDomainService.createAssetAccount(
                        "ETF", "Long term", "VOO", 450.25, 390.10, 50000.0);

        assertEquals("acc-3", account.getId());
        assertEquals(AccountType.ASSET, account.getType());
        assertEquals("VOO", account.getAsset());
        assertEquals(450.25, account.getCurrentPrice());
        assertEquals(390.10, account.getAverageCost());
        assertEquals(50000.0, account.getGoal());
    }
}

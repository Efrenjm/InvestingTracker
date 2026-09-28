package org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.domain.ports.inbound.WalletPort;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.AddMemberRequest;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.CreateWalletRequest;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.UpdateWalletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WalletManagementControllerTest {

    @Mock private WalletPort walletService;

    @Mock private UserPort userService;

    @InjectMocks private WalletManagementController controller;

    private UserIdentity user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = new UserIdentity("user-123", Set.of());
        wallet =
                Wallet.builder()
                        .id("wallet-1")
                        .name("My Wallet")
                        .description("Desc")
                        .visibility(Visibility.PRIVATE)
                        .build();
    }

    @Test
    void getMyWalletsReturnsUserWallets() {
        WalletSummary summary =
                WalletSummary.builder()
                        .id("wallet-1")
                        .name("My Wallet")
                        .description("Desc")
                        .roles(List.of())
                        .build();
        when(userService.getWallets(user)).thenReturn(Flux.just(summary));

        Flux<WalletSummary> result = controller.getMyWallets(user);

        StepVerifier.create(result).expectNext(summary).verifyComplete();
    }

    @Test
    void getPublicWalletsReturnsPublicWallets() {
        wallet.setVisibility(Visibility.PUBLIC);
        when(walletService.getPublicWallets()).thenReturn(Flux.just(wallet));

        Flux<Wallet> result = controller.getPublicWallets();

        StepVerifier.create(result).expectNext(wallet).verifyComplete();
    }

    @Test
    void getWalletReturnsWalletDetails() {
        when(walletService.getWalletById(user, "wallet-1")).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = controller.getWallet(user, "wallet-1");

        StepVerifier.create(result).expectNext(wallet).verifyComplete();
    }

    @Test
    void createWalletValidRequestCreatesWallet() {
        CreateWalletRequest request =
                new CreateWalletRequest("My Wallet", "Desc", Visibility.PUBLIC);
        when(walletService.createWallet(user, "My Wallet", "Desc", Visibility.PUBLIC))
                .thenReturn(Mono.just(wallet));

        Mono<Wallet> result = controller.createWallet(user, request);

        StepVerifier.create(result).expectNext(wallet).verifyComplete();
    }

    @Test
    void updateWalletValidRequestUpdatesWallet() {
        UpdateWalletRequest request =
                new UpdateWalletRequest("Updated Wallet", "Updated Desc", Visibility.PRIVATE);
        when(walletService.updateWallet(
                        user, "wallet-1", "Updated Wallet", "Updated Desc", Visibility.PRIVATE))
                .thenReturn(Mono.just(wallet));

        Mono<Wallet> result = controller.updateWallet(user, "wallet-1", request);

        StepVerifier.create(result).expectNext(wallet).verifyComplete();
    }

    @Test
    void deleteWalletValidRequestDeletesWallet() {
        when(walletService.deleteWallet(user, "wallet-1")).thenReturn(Mono.empty());

        Mono<Void> result = controller.deleteWallet(user, "wallet-1");

        StepVerifier.create(result).verifyComplete();

        verify(walletService).deleteWallet(user, "wallet-1");
    }

    @Test
    void addMemberValidRequestAddsMember() {
        AddMemberRequest request = new AddMemberRequest("user-456", "Manager");
        when(walletService.addMember(user, "wallet-1", "user-456", "Manager"))
                .thenReturn(Mono.empty());

        Mono<Void> result = controller.addMember(user, "wallet-1", request);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void removeMemberValidRequestRemovesMember() {
        when(walletService.removeMember(user, "wallet-1", "user-456")).thenReturn(Mono.empty());

        Mono<Void> result = controller.removeMember(user, "wallet-1", "user-456");

        StepVerifier.create(result).verifyComplete();
    }
}

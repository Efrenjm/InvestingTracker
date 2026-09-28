package org.efrenjm.investingtracker.application.service.wallet_management;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.efrenjm.investingtracker.application.service.user_service.exceptions.UserNotFoundException;
import org.efrenjm.investingtracker.application.service.wallet_management.exceptions.UnauthorizedActionException;
import org.efrenjm.investingtracker.application.service.wallet_management.exceptions.UnauthorizedWalletAccessException;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.service.WalletDomainService;
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
class WalletServiceTest {

    @Mock private WalletRepositoryPort walletRepository;

    @Mock private UserRepositoryPort userRepository;

    @Mock private WalletDomainService walletDomainService;

    @InjectMocks private WalletService walletService;

    private UserIdentity ownerIdentity;
    private UserIdentity managerIdentity;
    private UserIdentity otherUserIdentity;
    private Wallet testWallet;
    private User ownerUser;
    private User memberUser;

    @BeforeEach
    void setUp() {
        ownerIdentity = new UserIdentity("user-owner", Set.of());
        managerIdentity = new UserIdentity("user-manager", Set.of());
        otherUserIdentity = new UserIdentity("user-other", Set.of());

        Map<String, Role> roles = new HashMap<>();
        roles.put(
                "Owner",
                Role.builder()
                        .description("Owner")
                        .members(new HashSet<>(List.of("user-owner")))
                        .build());
        roles.put(
                "Manager",
                Role.builder()
                        .description("Manager")
                        .members(new HashSet<>(List.of("user-manager")))
                        .build());

        testWallet =
                Wallet.builder()
                        .id("wallet-123")
                        .name("Test Wallet")
                        .description("Description")
                        .visibility(Visibility.PRIVATE)
                        .roles(roles)
                        .build();

        ownerUser = User.builder().id("user-owner").username("owner@example.com").build();
        memberUser = User.builder().id("user-manager").username("manager@example.com").build();
    }

    @Test
    void createWalletValidSavesAndLinksOwner() {
        when(walletDomainService.createWallet(
                        "user-owner", "New Wallet", "Desc", Visibility.PRIVATE))
                .thenReturn(testWallet);
        when(walletRepository.save(testWallet)).thenReturn(Mono.just(testWallet));
        when(userRepository.findById("user-owner")).thenReturn(Mono.just(ownerUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(ownerUser));

        StepVerifier.create(
                        walletService.createWallet(
                                ownerIdentity, "New Wallet", "Desc", Visibility.PRIVATE))
                .expectNext(testWallet)
                .verifyComplete();

        verify(walletRepository).save(testWallet);
        verify(userRepository).save(ownerUser);
    }

    @Test
    void updateWalletOwnerSuccess() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(
                        walletService.updateWallet(
                                ownerIdentity,
                                "wallet-123",
                                "Updated Name",
                                "Updated Desc",
                                Visibility.PUBLIC))
                .expectNextMatches(
                        updated ->
                                updated.getName().equals("Updated Name")
                                        && updated.getVisibility() == Visibility.PUBLIC)
                .verifyComplete();
    }

    @Test
    void updateWalletUnauthorizedUserThrowsException() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));

        StepVerifier.create(
                        walletService.updateWallet(
                                otherUserIdentity,
                                "wallet-123",
                                "Updated Name",
                                "Desc",
                                Visibility.PRIVATE))
                .expectError(UnauthorizedActionException.class)
                .verify();
    }

    @Test
    void deleteWalletOwnerSuccess() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));
        when(walletRepository.delete("wallet-123")).thenReturn(Mono.empty());

        StepVerifier.create(walletService.deleteWallet(ownerIdentity, "wallet-123"))
                .verifyComplete();

        verify(walletRepository).delete("wallet-123");
    }

    @Test
    void deleteWalletManagerThrowsUnauthorizedActionException() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));

        StepVerifier.create(walletService.deleteWallet(managerIdentity, "wallet-123"))
                .expectError(UnauthorizedActionException.class)
                .verify();
    }

    @Test
    void getWalletByIdPrivateOwnerAccessSuccess() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));

        StepVerifier.create(walletService.getWalletById(ownerIdentity, "wallet-123"))
                .expectNext(testWallet)
                .verifyComplete();
    }

    @Test
    void getWalletByIdPrivateNonMemberThrowsUnauthorizedWalletAccessException() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));

        StepVerifier.create(walletService.getWalletById(otherUserIdentity, "wallet-123"))
                .expectError(UnauthorizedWalletAccessException.class)
                .verify();
    }

    @Test
    void getWalletByIdPublicNonMemberSuccess() {
        testWallet.setVisibility(Visibility.PUBLIC);
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));

        StepVerifier.create(walletService.getWalletById(otherUserIdentity, "wallet-123"))
                .expectNext(testWallet)
                .verifyComplete();
    }

    @Test
    void getPublicWalletsReturnsPublicWallets() {
        when(walletRepository.findByVisibility(Visibility.PUBLIC))
                .thenReturn(Flux.just(testWallet));

        StepVerifier.create(walletService.getPublicWallets())
                .expectNext(testWallet)
                .verifyComplete();
    }

    @Test
    void addMemberOwnerSuccess() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));
        when(userRepository.findById("user-manager")).thenReturn(Mono.just(memberUser));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(testWallet));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(memberUser));

        StepVerifier.create(
                        walletService.addMember(
                                ownerIdentity, "wallet-123", "user-manager", "Manager"))
                .verifyComplete();
    }

    @Test
    void addMemberUserNotFoundThrowsUserNotFoundException() {
        when(walletRepository.findById("wallet-123")).thenReturn(Mono.just(testWallet));
        when(userRepository.findById("unknown-user")).thenReturn(Mono.empty());

        StepVerifier.create(
                        walletService.addMember(
                                ownerIdentity, "wallet-123", "unknown-user", "Manager"))
                .expectError(UserNotFoundException.class)
                .verify();
    }
}

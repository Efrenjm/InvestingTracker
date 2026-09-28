package org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.UserPort;
import org.efrenjm.investingtracker.domain.ports.inbound.WalletPort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.AddMemberRequest;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.CreateWalletRequest;
import org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto.UpdateWalletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletManagementController {

    private final WalletPort walletService;
    private final UserPort userService;

    @GetMapping
    public Flux<WalletSummary> getMyWallets(@AuthUser UserIdentity user) {
        return userService.getWallets(user);
    }

    @GetMapping("/public")
    public Flux<Wallet> getPublicWallets() {
        return walletService.getPublicWallets();
    }

    @GetMapping("/{walletId}")
    public Mono<Wallet> getWallet(@AuthUser UserIdentity user, @PathVariable String walletId) {
        return walletService.getWalletById(user, walletId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Wallet> createWallet(
            @AuthUser UserIdentity user, @Valid @RequestBody CreateWalletRequest request) {
        return walletService.createWallet(
                user, request.name(), request.description(), request.visibility());
    }

    @PutMapping("/{walletId}")
    public Mono<Wallet> updateWallet(
            @AuthUser UserIdentity user,
            @PathVariable String walletId,
            @Valid @RequestBody UpdateWalletRequest request) {
        return walletService.updateWallet(
                user, walletId, request.name(), request.description(), request.visibility());
    }

    @DeleteMapping("/{walletId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteWallet(@AuthUser UserIdentity user, @PathVariable String walletId) {
        return walletService.deleteWallet(user, walletId);
    }

    @PostMapping("/{walletId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addMember(
            @AuthUser UserIdentity user,
            @PathVariable String walletId,
            @Valid @RequestBody AddMemberRequest request) {
        return walletService.addMember(user, walletId, request.memberId(), request.roleName());
    }

    @DeleteMapping("/{walletId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeMember(
            @AuthUser UserIdentity user,
            @PathVariable String walletId,
            @PathVariable String memberId) {
        return walletService.removeMember(user, walletId, memberId);
    }
}

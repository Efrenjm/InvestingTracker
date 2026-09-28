package org.efrenjm.investingtracker.domain.model.account;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.efrenjm.investingtracker.domain.model.account.exceptions.TagAlreadyExistsException;
import org.efrenjm.investingtracker.domain.model.account.exceptions.TagNotFoundException;
import org.efrenjm.investingtracker.domain.model.account.exceptions.WalletAlreadySharingAccountException;
import org.efrenjm.investingtracker.domain.model.account.exceptions.WalletNotSharingAccountException;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseAccount extends AuditableModel {
    protected String name;

    protected String description;

    protected String walletId;

    @Builder.Default protected Set<String> sharingWallets = new HashSet<>();

    protected AccountType type;

    @Builder.Default protected Double available = 0.0;

    @Builder.Default protected Set<String> tags = new HashSet<>();

    protected AccountConfig accountConfig;

    @Builder.Default protected Set<String> rules = new HashSet<>();

    public Optional<Set<String>> getSharingWallets() {
        return Optional.ofNullable(sharingWallets);
    }

    public Optional<Set<String>> getTags() {
        return Optional.ofNullable(tags);
    }

    public Optional<Set<String>> getRules() {
        return Optional.ofNullable(rules);
    }

    public void addSharingWallet(String walletId) {
        ensureSharingWallets();
        if (sharingWallets.contains(walletId)) {
            throw new WalletAlreadySharingAccountException(walletId);
        }

        sharingWallets.add(walletId);
    }

    public void removeSharingWallet(String walletId) {
        ensureSharingWallets();
        if (!sharingWallets.contains(walletId)) {
            throw new WalletNotSharingAccountException(walletId);
        }

        sharingWallets.remove(walletId);
    }

    public void addTag(String tag) {
        ensureTags();
        if (tags.contains(tag)) {
            throw new TagAlreadyExistsException(tag);
        }

        tags.add(tag);
    }

    public void removeTag(String tag) {
        ensureTags();
        if (!tags.contains(tag)) {
            throw new TagNotFoundException(tag);
        }

        tags.remove(tag);
    }

    private void ensureSharingWallets() {
        if (sharingWallets == null) {
            sharingWallets = new HashSet<>();
        }
    }

    private void ensureTags() {
        if (tags == null) {
            tags = new HashSet<>();
        }
    }
}

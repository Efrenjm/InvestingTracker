package org.efrenjm.investingtracker.domain.model.user;

import java.util.Date;
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
import org.efrenjm.investingtracker.domain.model.BaseModel;
import org.efrenjm.investingtracker.domain.model.user.exceptions.FriendRequestNotFoundException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NotFriendsException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.PendingFriendRequestException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.WalletAlreadyLinkedToUserException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.WalletNotLinkedToUserException;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class User extends BaseModel {
    private String username;

    private String email;

    private String phoneNumber;

    private String password;

    @Builder.Default private boolean active = false;

    private VerificationRequest verificationRequest;

    @Builder.Default private Set<SystemRole> roles = new HashSet<>();

    private String firstName;

    private String middleName;

    private String lastName;

    private String profilePicture;

    @Builder.Default private Set<String> wallets = new HashSet<>();

    @Builder.Default private Set<String> friends = new HashSet<>();

    @Builder.Default private Set<String> pendingFriends = new HashSet<>();

    @Builder.Default private Set<String> invitedFriends = new HashSet<>();

    private UserPreferences preferences;

    private Date lastLogin;

    public Optional<VerificationRequest> getVerificationRequest() {
        return Optional.ofNullable(this.verificationRequest);
    }

    public Optional<Set<String>> getWallets() {
        return Optional.ofNullable(this.wallets);
    }

    public Optional<Set<String>> getFriends() {
        return Optional.ofNullable(this.friends);
    }

    public Optional<Set<String>> getPendingFriends() {
        return Optional.ofNullable(this.pendingFriends);
    }

    public Optional<Set<String>> getInvitedFriends() {
        return Optional.ofNullable(this.invitedFriends);
    }

    public boolean isEnabled() {
        return active;
    }

    public boolean isNewUser() {
        return email == null && phoneNumber == null;
    }

    public void clearVerificationRequest() {
        verificationRequest = null;
    }

    public void linkWallet(Wallet wallet, String roleName) {
        ensureWalletSet();
        String walletId = wallet.getId();
        if (this.wallets.contains(walletId)) {
            throw new WalletAlreadyLinkedToUserException(walletId, this.id);
        }
        wallet.addMemberToRole(roleName, this.id);
        this.wallets.add(wallet.getId());
    }

    public void unlinkWallet(Wallet wallet) {
        ensureWalletSet();
        String walletId = wallet.getId();
        if (!this.wallets.contains(walletId)) {
            throw new WalletNotLinkedToUserException(walletId, this.id);
        }
        wallet.removeMember(this.id);
        this.wallets.remove(walletId);
    }

    public void inviteFriend(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (this.invitedFriends.contains(friendId)) {
            throw new PendingFriendRequestException(true, friendId);
        }
        if (this.pendingFriends.contains(friendId)) {
            throw new PendingFriendRequestException(false, friendId);
        }
        friend.receiveFriendInvite(this);
        this.pendingFriends.add(friendId);
    }

    public void cancelFriendInvite(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (!this.invitedFriends.contains(friendId)) {
            throw new FriendRequestNotFoundException(friendId, true);
        }
        friend.removeFriendInvite(this);
        this.invitedFriends.remove(friendId);
    }

    public void acceptFriend(User friend) {
        ensureFriendSets();
        String userId = friend.getId();
        if (!this.pendingFriends.contains(userId)) {
            throw new FriendRequestNotFoundException(userId, false);
        }
        this.pendingFriends.remove(userId);
        friend.friendInvitationAccepted(this);
        this.friends.add(userId);
    }

    public void rejectFriend(User friend) {
        ensureFriendSets();

        String userId = friend.getId();
        if (!this.pendingFriends.contains(userId)) {
            throw new FriendRequestNotFoundException(userId, false);
        }
        this.pendingFriends.remove(userId);
        friend.friendInvitationRejected(this);
    }

    public void removeFriend(User friend) {
        ensureFriendSets();
        String userId = friend.getId();
        if (!this.friends.contains(userId)) {
            throw new NotFriendsException(this.id, userId);
        }
        this.friends.remove(userId);
        try {
            friend.removeFriend(this);
        } catch (Exception e) {
            /* Skip the third recursion */
        }
    }

    private void receiveFriendInvite(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (this.pendingFriends.contains(friendId)) {
            throw new PendingFriendRequestException(false, friendId);
        }
        if (this.invitedFriends.contains(friendId)) {
            throw new PendingFriendRequestException(true, friendId);
        }
        this.invitedFriends.add(friendId);
    }

    private void removeFriendInvite(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (!this.pendingFriends.contains(friendId)) {
            throw new FriendRequestNotFoundException(friendId, false);
        }
        this.pendingFriends.remove(friendId);
    }

    private void friendInvitationAccepted(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (!this.invitedFriends.contains(friendId)) {
            throw new FriendRequestNotFoundException(friendId, true);
        }
        this.invitedFriends.remove(friendId);
        this.friends.add(friendId);
    }

    private void friendInvitationRejected(User friend) {
        ensureFriendSets();
        String friendId = friend.getId();
        if (!this.invitedFriends.contains(friendId)) {
            throw new FriendRequestNotFoundException(friendId, true);
        }
        this.invitedFriends.remove(friendId);
    }

    private void ensureWalletSet() {
        if (this.wallets == null) {
            this.wallets = new HashSet<>();
        }
    }

    private void ensureFriendSets() {
        if (this.friends == null) {
            this.friends = new HashSet<>();
        }
        if (this.pendingFriends == null) {
            this.pendingFriends = new HashSet<>();
        }
        if (this.invitedFriends == null) {
            this.invitedFriends = new HashSet<>();
        }
    }
}

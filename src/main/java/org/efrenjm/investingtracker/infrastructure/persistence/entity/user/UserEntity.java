package org.efrenjm.investingtracker.infrastructure.persistence.entity.user;

import lombok.*;

import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.UserPreferences;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.BaseMongoEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Document(collection = "users")
public class UserEntity extends BaseMongoEntity
{
    @Field("username") private String username;
    @Field("email") private String email;
    @Field("phone_number") private String phoneNumber;
    @Field("password") private String password;
    @Field("active") private boolean active;
    @Field("verification_request") private EntityVerificationRequest verificationRequest;
    @Field("roles") private Set<SystemRole> roles;
    @Field("first_name") private String firstName;
    @Field("middle_name") private String middleName;
    @Field("last_name") private String lastName;
    @Field("profile_picture") private String profilePicture;
    @Field("wallets") private Set<ObjectId> wallets;
    @Field("friends") private Set<ObjectId> friends;
    @Field("pending_friends") private Set<ObjectId> pendingFriends;
    @Field("invited_friends") private Set<ObjectId> invitedFriends;
    @Field("preferences") private UserEntityPreferences preferences;
    @Field("last_login") private Date lastLogin;

    public static UserEntity fromDomain(User user)
    {
        if (user == null)
            return null;

        return populateBaseEntityFields(UserEntity.builder(), user)
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .active(user.isActive())
                .verificationRequest(EntityVerificationRequest.fromDomain(user.getVerificationRequest().orElse(null)))
                .roles(user.getRoles())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .wallets(MongoUtils.tryParseIds(user.getWallets().orElse(Set.of())))
                .friends(MongoUtils.tryParseIds(user.getFriends().orElse(Set.of())))
                .pendingFriends(MongoUtils.tryParseIds(user.getPendingFriends().orElse(Set.of())))
                .invitedFriends(MongoUtils.tryParseIds(user.getInvitedFriends().orElse(Set.of())))
                .preferences(UserEntityPreferences.fromDomain(user.getPreferences()))
                .lastLogin(user.getLastLogin())
                .build();
    }

    public User toDomain()
    {
        return populateBaseDomainFields(User.builder())
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(password)
                .active(active)
                .verificationRequest(Optional.ofNullable(verificationRequest)
                        .map(EntityVerificationRequest::toDomain).orElse(null))
                .roles(Optional.ofNullable(roles).orElse(new HashSet<>()))
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .profilePicture(profilePicture)
                .wallets(MongoUtils.collectIds(wallets))
                .friends(MongoUtils.collectIds(friends))
                .pendingFriends(MongoUtils.collectIds(pendingFriends))
                .invitedFriends(MongoUtils.collectIds(invitedFriends))
                .preferences(preferences != null ? preferences.toDomain() : null)
                .lastLogin(lastLogin)
                .build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    public static class EntityVerificationRequest
    {
        @Field("verification_code") private String code;
        @Field("code_usage") private CodeUsage codeUsage;
        @Field("credential") private String credential;
        @Field("expiration_date") private Date expiration;
        @Field("refresh_pause") private Date refreshPause;

        public static EntityVerificationRequest fromDomain(VerificationRequest request)
        {
            if (request == null)
                return null;

            return EntityVerificationRequest.builder()
                    .code(request.getCode())
                    .codeUsage(request.getCodeUsage())
                    .credential(request.getCredential())
                    .expiration(request.getExpiration())
                    .refreshPause(request.getRefreshPause())
                    .build();
        }

        public VerificationRequest toDomain()
        {
            return VerificationRequest.builder()
                    .code(code)
                    .codeUsage(codeUsage)
                    .credential(credential)
                    .expiration(expiration)
                    .refreshPause(refreshPause)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    public static class UserEntityPreferences
    {
        @Field("is_email_public") private boolean isEmailPublic;
        @Field("is_phone_public") private boolean isPhonePublic;
        @Field("is_name_public") private boolean isNamePublic;
        @Field("is_profile_public") private boolean isProfilePublic;

        public static UserEntityPreferences fromDomain(UserPreferences preferences)
        {
            if (preferences == null)
                return null;

            return UserEntityPreferences.builder()
                    .isEmailPublic(preferences.isEmailPublic())
                    .isNamePublic(preferences.isNamePublic())
                    .isPhonePublic(preferences.isPhonePublic())
                    .isProfilePublic(preferences.isProfilePublic())
                    .build();
        }

        public UserPreferences toDomain()
        {
            return UserPreferences.builder()
                    .isEmailPublic(isEmailPublic)
                    .isNamePublic(isNamePublic)
                    .isPhonePublic(isPhonePublic)
                    .isProfilePublic(isProfilePublic)
                    .build();
        }
    }
}

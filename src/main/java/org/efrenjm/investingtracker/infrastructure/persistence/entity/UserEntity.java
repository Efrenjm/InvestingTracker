package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "users")
public class UserEntity {
    @Id @Field("_id") private ObjectId id;
    @Field("username") private String username;
    @Field("email") private String email;
    @Field("phone_number") private String phoneNumber;
    @Field("password") private String password;
    @Field("active") private boolean active = false;
    @Field("verification_request") private EntityVerificationRequest verificationRequest;
    @Field("roles") private Set<GrantedAuthority> roles = new HashSet<>();
    @Field("first_name") private String firstName;
    @Field("middle_name") private String middleName;
    @Field("last_name") private String lastName;
    @Field("profile_picture") private String profilePicture;
    @Field("wallets") private List<ObjectId> wallets;
    @Field("friends") private List<ObjectId> friends;
    @Field("created_at") private Date createdAt;
    @Field("updated_at") private Date updatedAt;
    @Field("last_login") private Date lastLogin;

    public Optional<EntityVerificationRequest> getVerificationRequest() {
        return Optional.ofNullable(verificationRequest);
    }

    public static UserEntity fromDomain(User user) {
        VerificationRequest request = null;
        if (user.getVerificationRequest().isPresent()) {
            request = user.getVerificationRequest().get();
        }
        return UserEntity.builder()
                .id(new ObjectId(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .active(user.isActive())
                .friends(user.getFriends().stream().map(ObjectId::new).toList())
                .wallets(user.getWallets().stream().map(ObjectId::new).toList())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles())
                .verificationRequest(EntityVerificationRequest.fromDomain(request))
                .build();
    }

    public User toDomain() {
        VerificationRequest request = null;
        if (getVerificationRequest().isPresent()) {
            request = getVerificationRequest().get().toDomain();
        }
        return User.builder()
                .id(id.toString())
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(password)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .profilePicture(profilePicture)
                .active(active)
                .friends(friends.stream().map(ObjectId::toString).toList())
                .wallets(wallets.stream().map(ObjectId::toString).toList())
                .lastLogin(lastLogin)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .roles(roles)
                .verificationRequest(request)
                .build();
    }

    @Builder
    @Getter
    @Setter
    @ToString
    public static class EntityVerificationRequest {
        @Field("verification_code") private String code;
        @Field("code_usage") private EntityCodeUsage entityCodeUsage;
        @Field("credential") private String credential;
        @Field("expiration_date") private Date expiration;
        @Field("refresh_pause") private Date refreshPause;

        public static EntityVerificationRequest fromDomain(VerificationRequest request) {
            if (request == null) {
                return null;
            }
            return EntityVerificationRequest.builder()
                    .code(request.getCode())
                    .entityCodeUsage(EntityCodeUsage.valueOf(request.getCodeUsage().name()))
                    .credential(request.getCredential())
                    .expiration(request.getExpiration())
                    .refreshPause(request.getRefreshPause())
                    .build();
        }

        public VerificationRequest toDomain() {
            return VerificationRequest.builder()
                    .code(code)
                    .codeUsage(entityCodeUsage.getCodeUsage())
                    .credential(credential)
                    .expiration(expiration)
                    .refreshPause(refreshPause)
                    .build();
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum EntityCodeUsage {
        EMAIL_VERIFICATION(CodeUsage.EMAIL_VERIFICATION),
        PHONE_VERIFICATION(CodeUsage.PHONE_VERIFICATION),
        PASSWORD_RESET(CodeUsage.PASSWORD_RESET);

        private final CodeUsage codeUsage;
    }
}

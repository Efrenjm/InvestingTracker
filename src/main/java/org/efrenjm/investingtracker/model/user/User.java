package org.efrenjm.investingtracker.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    @Field("_id")
    private ObjectId id;

    private String username;

    private String email;

    @Field("phone_number")
    private String phoneNumber;

    @Field("update_email_request")
    private String updateEmailRequest;

    @Field("update_phone_request")
    private String updatePhoneRequest;

    private String password;

    private boolean active = false;

    private String verificationCode;

    private Date codeExpiration;

    private CodeUsage codeUsage;

    private Set<GrantedAuthority> roles = new HashSet<>();

    @Field("first_name")
    private String firstName;

    @Field("middle_name")
    private String middleName;

    @Field("last_name")
    private String lastName;

    @Field("profile_picture")
    private String profilePicture;

    private List<ObjectId> organizations;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("last_login")
    private Date lastLogin;

    private List<ObjectId> friends;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public boolean isNewUser() {
        return email == null && phoneNumber == null;
    }

    public void codeVerified() {
        if (this.codeUsage.equals(CodeUsage.EMAIL_VERIFICATION)) {
            this.email = this.updateEmailRequest;
            this.updateEmailRequest = null;
        } else if (this.codeUsage.equals(CodeUsage.PHONE_VERIFICATION)) {
            this.phoneNumber = this.updatePhoneRequest;
            this.updatePhoneRequest = null;
        } else {
            throw new IllegalArgumentException("Invalid CodeUsage: " + this.codeUsage);
        }
        clearVerificationRequest();
    }

    public void clearVerificationRequest() {
        this.verificationCode = null;
        this.codeExpiration = null;
        this.codeUsage = null;
    }
}

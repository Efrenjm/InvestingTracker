package org.efrenjm.investingtracker.model.Auth;

import lombok.*;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Document(collection = "auth")
public class AuthCredentials implements UserDetails {
    @Id
    @Field("_id")
    private String id;

    private String username;

    private String email;

    @Field("phone_number")
    private String phoneNumber;

    private String password;

    private Profile profile;

    private boolean active = true;

    private Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();

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
}

package org.efrenjm.investingtracker.infrastructure.security;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
public class SecurityUserDetails implements UserDetails {
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private Set<SystemRole> authorities;
    private boolean enabled;

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(authorities).orElse(new HashSet<>()).stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public static SecurityUserDetails fromDomain(User user) {
        return SecurityUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .authorities(user.getRoles())
                .enabled(user.isActive())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(password)
                .roles(authorities != null ? authorities : new HashSet<>())
                .active(enabled)
                .build();
    }
}

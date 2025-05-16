package org.efrenjm.investingtracker.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
	private final transient User user;

	public User getDomainUser() {
		return this.user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getRoles();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return user.isActive();
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.isActive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return user.isActive();
	}

	@Override
	public boolean isEnabled() {
		return user.isActive();
	}

	public static SecurityUser fromDomain(User user) {
		return new SecurityUser(user);
	}

	public User toDomain() {
		return user;
	}
}

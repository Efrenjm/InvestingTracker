//package org.efrenjm.investingtracker.infrastructure.security;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.efrenjm.investingtracker.domain.model.user.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
//@RequiredArgsConstructor
//@NoArgsConstructor(force = true)
//public class SecurityUser implements UserDetails {
//
//
//	@JsonProperty("coreUser")
//	private final transient User user;
//
//	public User getDomainUser() {
//		return this.user;
//	}
//
//	@JsonProperty("id")
//	private String getSerializedId() {
//		return user.getId();
//	}
//
//	@JsonProperty("username")
//	@Override
//	public String getUsername() {
//		return user.getUsername();
//	}
//
//	@JsonProperty("email")
//	public String getEmail() {
//		return user.getEmail();
//	}
//
//	@JsonProperty("phone")
//	public String getPhoneNumber() {
//		return user.getPhoneNumber();
//	}
//
//	@JsonProperty("wallets")
//	public List<String> getWallets() {
//		return user.getWallets();
//	}
//
//	@JsonProperty("roles")
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return user.getRoles();
//	}
//
//	@JsonIgnore
//	@Override
//	public String getPassword() {
//		return user.getPassword();
//	}
//
//	@JsonIgnore
//	@Override
//	public boolean isAccountNonExpired() {
//		return user.isActive();
//	}
//
//	@JsonIgnore
//	@Override
//	public boolean isAccountNonLocked() {
//		return user.isActive();
//	}
//
//	@JsonIgnore
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return user.isActive();
//	}
//
//	@JsonIgnore
//	@Override
//	public boolean isEnabled() {
//		return user.isActive();
//	}
//
//	public static SecurityUser fromDomain(User user) {
//		return new SecurityUser(user);
//	}
//
//	public User toDomain() {
//		return user;
//	}
//}

package org.efrenjm.investingtracker.interfaces.web.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter
{
	private final SecurityService securityService;

	@Override
	@NonNull
	public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain)
	{
		String token = getTokenFromCookie(exchange);

		if (!StringUtils.hasText(token) || !securityService.isValidToken(token))
		{
			return chain.filter(exchange);
		}
		String userId = securityService.extractUserId(token);
		Set<SystemRole> roles = securityService.extractRoles(token);

		Set<GrantedAuthority> grantedAuthorities = Optional.ofNullable(roles).orElse(new HashSet<>()).stream()
				.map(role -> new SimpleGrantedAuthority(role.name()))
				.collect(Collectors.toSet());


		UserIdentity userIdentity = new UserIdentity(userId, roles);

		exchange.getAttributes().put("authUser", userIdentity);
		UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(userIdentity, null, grantedAuthorities);

		return chain.filter(exchange)
				.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));

//		return securityService.loadUserByUserId(userId)
//				.flatMap(userSession -> {
//					exchange.getAttributes().put("authUser", userSession);
//					UsernamePasswordAuthenticationToken authToken =
//							new UsernamePasswordAuthenticationToken(userSession, null, userSession.getRoles());
//
//					return chain.filter(exchange)
//							.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
//				});
	}

	public String getTokenFromCookie(ServerWebExchange exchange) {
		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
		return cookie != null ? cookie.getValue() : null;
	}
}

package org.efrenjm.investingtracker.interfaces.web.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
	private final SecurityService securityService;

	@Override
	@NonNull
	public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
		String token = getTokenFromCookie(exchange);

		if (!StringUtils.hasText(token) || !securityService.isValidToken(token)) {
			return chain.filter(exchange);
		}

		return securityService.loadUserByUserId(securityService.extractUserId(token))
				.flatMap(userDetails -> {
					exchange.getAttributes().put("authenticatedUser", userDetails);

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
				});
	}

	public String getTokenFromCookie(ServerWebExchange exchange) {
		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
		return cookie != null ? cookie.getValue() : null;
	}
}

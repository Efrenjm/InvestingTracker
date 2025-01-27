package org.efrenjm.investingtracker.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter {
	private final JwtService jwtService;
	private final ReactiveUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, ReactiveUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String token = extractToken(exchange.getRequest());

		if (StringUtils.hasText(token) && jwtService.isTokenValid(token)) {

			String username = jwtService.extractUsername(token);
			return userDetailsService.findByUsername(username)
					.flatMap(userDetails -> {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
					});
		}
		return chain.filter(exchange);
	}

	private String extractToken(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}

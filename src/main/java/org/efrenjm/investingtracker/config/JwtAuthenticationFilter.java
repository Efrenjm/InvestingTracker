package org.efrenjm.investingtracker.config;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.service.authentication.CustomUserDetailsService;
import org.efrenjm.investingtracker.service.utils.JwtService;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter {
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	@NonNull
	public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
		String token = getTokenFromCookie(exchange);

		if (StringUtils.hasText(token) && jwtService.isTokenValid(token)) {

			ObjectId userId = new ObjectId(jwtService.extractUserId(token));

			return userDetailsService.findById(userId)
					.flatMap(userDetails -> {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
					});
		}
		return chain.filter(exchange);
	}

	public String getTokenFromCookie(ServerWebExchange exchange) {
		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
		return cookie != null ? cookie.getValue() : null;
	}
}

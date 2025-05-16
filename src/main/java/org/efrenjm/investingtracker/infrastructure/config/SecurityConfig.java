package org.efrenjm.investingtracker.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.efrenjm.investingtracker.interfaces.web.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final SecurityService securityService;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.authorizeExchange(exchange -> exchange
						.pathMatchers(
								"/auth/login",
								"/auth/register",
								"/auth/refresh-code",
								"/auth/verify-code"
						).permitAll()
						.anyExchange().authenticated()
				)
				.addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(securityService);
	}
}

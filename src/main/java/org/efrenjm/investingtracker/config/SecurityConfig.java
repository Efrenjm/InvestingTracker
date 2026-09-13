package org.efrenjm.investingtracker.config;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.service.authentication.CustomUserDetailsService;
import org.efrenjm.investingtracker.service.utils.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

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
								"/auth/refresh-code/**",
								"/auth/verify-code"
						).permitAll()
						.anyExchange().authenticated()
				)
				.addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtService, userDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
